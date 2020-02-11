/*
 * serial.h
 *
 * Created: 29.03.2019 21:48:32
 *  Author: RUSLAN
 */ 


#ifndef SERIAL_H_
#define SERIAL_H_

#define SERIAL_BUFFER_SIZE 64

extern unsigned char queueC, sendC;  //current and send byte
extern char queue[50];
extern char buffer_digits[12];

extern char _rx_buffer[SERIAL_BUFFER_SIZE];
extern char _tx_buffer[SERIAL_BUFFER_SIZE];
extern volatile unsigned char _rx_buffer_head;
extern volatile unsigned char _rx_buffer_tail;

extern volatile unsigned char rx_flag;
extern volatile unsigned char tx_flag;
extern unsigned char car_data[30]; // 19

void serial_init();
void SendStr(char *s);


#endif /* SERIAL_H_ */