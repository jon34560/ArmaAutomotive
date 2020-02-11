/*
 * timers.h
 *
 * Created: 07.04.2019 11:33:16
 *  Author: RUSLAN
 */ 


#ifndef TIMERS_H_
#define TIMERS_H_

//extern uint16_t TIMER0_Value;
extern uint16_t PWM1_Value;
extern uint16_t PWM2_Value;
extern uint16_t PWM3_Value;
extern uint16_t PWM4_Value;
extern uint16_t PWM5_Value;
extern uint16_t PWM6_Value;

extern uint16_t Wheel1_Value;
extern uint16_t Wheel2_Value;
extern uint16_t Wheel3_Value;
extern uint16_t Wheel4_Value;

extern volatile uint8_t Wheel_waits;

extern volatile uint8_t LT_delay;
extern volatile uint8_t RT_delay;
extern volatile uint8_t Hz_delay;
extern volatile uint8_t beam_delay;
extern volatile uint8_t LRT_periods;
extern volatile uint8_t LW_periods;
extern volatile uint8_t RW_periods;
extern volatile uint8_t L1_periods;
extern volatile uint8_t L2_periods;
extern volatile uint8_t L3_periods;
extern volatile uint8_t L4_periods;

void timer0_init();
void timer0_stop();
void timer2_init();
void timer2_stop();
void timers_init();
void ints_enable(uint8_t int_num);


#endif /* TIMERS_H_ */