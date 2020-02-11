/*
 * serial.c
 *
 * Created: 29.03.2019 21:47:32
 *  Author: RUSLAN
 */ 

#include "serial.h"
#include <atmel_start.h>
#include <string.h>

//#define baudrate 9600L
//#define baudrate 19200L
//#define baudrate 38400L
#define baudrate 57600L
//#define baudrate 115200L // too high

#define bauddivider (F_CPU/(16*baudrate)-1)
#define HI(x) ((x)>>8)
#define LO(x) ((x)& 0xFF)


char _rx_buffer[SERIAL_BUFFER_SIZE];
volatile unsigned char _rx_buffer_head = 0;
volatile unsigned char _rx_buffer_tail = 0;

char _tx_buffer[SERIAL_BUFFER_SIZE];
volatile unsigned char _tx_buffer_head = 0;
volatile unsigned char _tx_buffer_tail = 0;

volatile uint8_t   uartReadyTx;			///< uartReadyTx flag
volatile uint8_t   uartBufferedTx;		///< uartBufferedTx flag

unsigned char queueC, sendC;  //current and send byte
char queue[50];

volatile unsigned char rx_flag = 0;
volatile unsigned char tx_flag = 0;
unsigned char car_data[30]; // 19
char buffer_digits[12];

//Interrupt empty buffer
ISR (USART0_UDRE_vect)
{	
	if (queueC != sendC) {
		UDR0 = _tx_buffer[sendC++];
	}
	else
	{
		UCSR0B &=~(1<<UDRIE0);	// Interrupt disable - transmit finish
		tx_flag = 1;
	}
}

ISR(USART0_RX_vect)
{
	uint8_t c = UDR0;
	uint8_t next_rx_tail = (_rx_buffer_tail + 1) % SERIAL_BUFFER_SIZE;
	
	if (next_rx_tail != _rx_buffer_head)
	{
		if (c != 0x0D)  {
		_rx_buffer[_rx_buffer_tail] = c;
		_rx_buffer_tail = next_rx_tail;
		}
		else {
			rx_flag = 1;
		};
	}
}

void serial_init()
{
	UBRR0L = LO(bauddivider);
	UBRR0H = HI(bauddivider);
	UCSR0A = 0;
	UCSR0B = 1<<RXEN0|1<<TXEN0|1<<RXCIE0|0<<TXCIE0;
	UCSR0C = 1<<UCSZ0|1<<UCSZ01;

	for(int i = 0; i < 20; i++){
		car_data[i] = 0;
	}
}

void SendStr(char *s)
{
	queueC = 0;	//first byte
	sendC = 0;
	while (*s) _tx_buffer[queueC++] = *s++;
	while (buffer_digits[sendC]) _tx_buffer[queueC++] = buffer_digits[sendC++];
	sendC = 1;	//first byte transmit
	_tx_buffer[queueC++] = 0x0D;   	//end str
	_tx_buffer[queueC++] = 0x0A;   	//return
	
	UDR0 = _tx_buffer[0];  		//Transmit first byte
	UCSR0B|=(1<<UDRIE0);	// Enable UDRE
}


