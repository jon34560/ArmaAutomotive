/*
 * timers.c
 *
 * Created: 07.04.2019 11:32:59
 *  Author: RUSLAN
 */ 

#include <atmel_start.h>

uint16_t PWM1_Value = 0;
uint16_t PWM2_Value = 0;
uint16_t PWM3_Value = 0;
uint16_t PWM4_Value = 0;
uint16_t PWM5_Value = 0;
uint16_t PWM6_Value = 0;

volatile uint16_t Wheel1_Value = 0;
volatile uint16_t Wheel2_Value = 0;
volatile uint16_t Wheel3_Value = 0;
volatile uint16_t Wheel4_Value = 0;

volatile uint32_t Wheel_Timer = 0;
//uint16_t Wheel2_Timer = 0;
//uint16_t Wheel3_Timer = 0;
//uint16_t Wheel4_Timer = 0;

//uint16_t TIMER0_Value = 0;
volatile uint8_t LT_delay = 0;
volatile uint8_t RT_delay = 0;
volatile uint8_t Hz_delay = 0;
volatile uint8_t beam_delay = 0;
volatile uint8_t LRT_periods = 0;
volatile uint8_t LW_periods = 0;
volatile uint8_t RW_periods = 0;
volatile uint8_t L1_periods = 0;
volatile uint8_t L2_periods = 0;
volatile uint8_t L3_periods = 0;
volatile uint8_t L4_periods = 0;

volatile uint8_t int0_order = 0;
volatile uint8_t int1_order = 0;
volatile uint8_t int2_order = 0;
volatile uint8_t int3_order = 0;

volatile uint8_t Wheel_waits = 0;

void timer0_init()
{
	//TIMER0
	// initialize counter
	TCNT0 = 6;	//6 - for 16ms, 100 - for 10ms
	TIMSK |= (1<<TOIE0);
	// set up timer with prescaler = 1024
	TCCR0 |= (1 << CS02)|(1 << CS01)|(1 << CS00);
}

void timer0_stop()
{
	//TIMER0
	TCCR0 = 0;
	TIMSK &= ~(1<<TOIE0);	// Interrupt disable
}

void timer2_init()
{
	//TIMER2
	// initialize counter
	TCNT2 = 206;	//0,025 ms (40 kHz)
	TIMSK |= (1<<TOIE2);
	if (TIFR |= (1<<TOV2)) TIFR |= (1<<TOV2);
	// set up timer with prescaler = 8
	//TCCR2 |= (1 << CS21);
}

void timer2_stop()
{
	//TIMER0
	//TCCR2 = 0;
	TIMSK &= ~(1<<TOIE2);	// Interrupt disable
}



///////////////Fast PWM mode initialization////////////////////
//
// Config. Timer/Counter1 to work in fast PWM mode:
// * Frequency: 16MHz/1024 = 15.625KHz
// * Resolution: 10 bit (top value = 0x03FF)
//
///////////////////////////////////////////////////////////////
void timers_init()
{
	TCCR0 = 0;
	
	//TIMER1 and TIMER3	
	// Use fixed top value 0x03FF (WGMn3:WGMn0 = 0b0111)
	// No Prescaling (CSn2:CSn0 = 0b001)

	//(1<<COM1A1)|(1<<COM1B1)|(1<<COM1C1)
	TCCR1A |= (1<<WGM11)|(1<<WGM10);
	TCCR1B |= (1<<WGM12)|(1<<CS10);
	OCR1A = 0;   // OUT initially
	OCR1B = 0;   // OUT initially
	OCR1C = 0;   // OUT initially
	//TIMSK |= (1<<TOIE1); // Enable overflow interrupt

	//(1<<COM3A1)|(1<<COM3B1)|(1<<COM3C1)
	TCCR3A |= (1<<WGM31)|(1<<WGM30);
	TCCR3B |= (1<<WGM32)|(1<<CS30);
	OCR3A = 0;   // OUT initially 
	OCR3B = 0;   // OUT initially 
	OCR3C = 0;   // OUT initially 
	//ETIMSK |= (1<<TOIE3); // Enable overflow interrupt
}

void ints_enable(uint8_t int_num){
	//The rising edge of INTn generates asynchronously an interrupt request.
	EICRA = (1 << ISC01)|(1 << ISC00)|(1 << ISC11)|(1 << ISC10)|(1 << ISC21)|(1 << ISC20)|(1 << ISC31)|(1 << ISC30);
	//EIMSK |= (1 << INT0)|(1 << INT1)|(1 << INT2)|(1 << INT3);
	
	Wheel_Timer = 0;
	
	switch (int_num) {
		case 0:				//INT0
		EIMSK = (1 << INT0);
	    break;
		case 1:				//INT1
		EIMSK = (1 << INT1);
		break;
		case 2:				//INT2
		EIMSK = (1 << INT2);
		break;
		case 3:				//INT3
		EIMSK = (1 << INT3);
		break;
	}
}

void ints_disable(){
	EIMSK = 0;
}

ISR(TIMER0_OVF_vect)	//TIMER 0 - TIME COUNTER
{
	TCNT0 = 6;
	if (LT_delay == 0 && RT_delay == 0 && Hz_delay == 0 && LRT_periods == 0 && beam_delay == 0 &&
		LW_periods ==0 && RW_periods == 0 && L1_periods ==0 && L2_periods == 0 && L3_periods ==0 && 
			L4_periods == 0 && Wheel_waits == 0) {
		timer0_stop();
	}
	else {
		if (LT_delay != 0) LT_delay--;
		if (RT_delay != 0) RT_delay--;
		if (Hz_delay != 0) Hz_delay--;
		if (beam_delay != 0) beam_delay--;
		if (LRT_periods != 0) LRT_periods--;
		if (RW_periods != 0) RW_periods--;
		if (LW_periods != 0) LW_periods--;
		if (L1_periods != 0) L1_periods--;
		if (L2_periods != 0) L2_periods--;
		if (L3_periods != 0) L3_periods--;
		if (L4_periods != 0) L4_periods--;
		if (Wheel_waits != 0) Wheel_waits--;
	}

}

ISR(TIMER2_OVF_vect)	//TIMER 2 - TIME COUNTER
{
	TCNT2 = 206;
	Wheel_Timer++;
	//Wheel2_Timer++;
	//Wheel3_Timer++;
	//Wheel4_Timer++;
}

ISR(TIMER1_OVF_vect)	//TIMER 1
{
	TIMSK &= ~(1<<TOIE1);	// Interrupt disable
	OCR1A = PWM1_Value;
	OCR1B = PWM2_Value;
	OCR1C = PWM3_Value;
}

ISR(TIMER3_OVF_vect)	//TIMER 3
{
	ETIMSK &= ~(1<<TOIE3);	// Interrupt disable
	OCR3A = PWM4_Value;
	OCR3B = PWM5_Value;
	OCR3C = PWM6_Value;
}

ISR (INT0_vect) {	// wheel-1 
	if (int0_order){
		Wheel1_Value = Wheel_Timer;
		timer2_stop();
		ints_disable();
		int0_order = 0;
	}
	else{
		TCCR2 |= (1 << CS21); //Start Timer 2 
		Wheel_Timer = 0;
		int0_order = 1;
	}
}

ISR (INT1_vect) {	// wheel-2 
	if (int1_order){
		Wheel2_Value = Wheel_Timer;
		timer2_stop();
		ints_disable();
		int1_order = 0;
	}
	else{
		TCCR2 |= (1 << CS21); //Start Timer 2 
		Wheel_Timer = 0;
		int1_order = 1;
	}
}

ISR (INT2_vect) {	// wheel-3 
	if (int2_order){
		Wheel3_Value = Wheel_Timer;
		timer2_stop();
		ints_disable();
		int2_order = 0;
	}
	else{
		TCCR2 |= (1 << CS21); //Start Timer 2 
		Wheel_Timer = 0;
		int2_order = 1;
	}
}

ISR (INT3_vect) {	// wheel-4 
	if (int3_order){
		Wheel4_Value = Wheel_Timer;
		timer2_stop();
		ints_disable();
		int3_order = 0;
	}
	else{
		TCCR2 |= (1 << CS21); //Start Timer 2 
		Wheel_Timer = 0;
		int3_order = 1;
	}
}

	