/*
 * serial_communication.c
 *
 * Created: 31.03.2019 3:14:33
 *  Author: RUSLAN
 *
 * Jan 2020 Jon Taylor  
 */ 


#include "serial_communication.h"
#include <atmel_start.h>
#include <avr/pgmspace.h>
#include <string.h>
#include <defines.h>
#include <serial.h>
#include <timers.h>
#include <adc.h>
#include <MCP41x.h>

//int nums=0;
int counter=0;
int digits=0;

void parcer()
{
	if (rx_flag) {
		rx_flag = 0;
		//buffer_digits[0]=0;
		//SendStr(_rx_buffer);
		//while (tx_flag == 0);
		//tx_flag = 0;
		int nums = 0;
		
		//SendStr("Hello ");
		
		// Read ?
		while (0 != strcmp(_rx_buffer, strcpy_P(queue, (PGM_P)pgm_read_word(&(InputStringPointers[nums])))) && nums < 21) nums++;
		
		switch (nums) {
			case 0:		// get command
				Wheel_waits = 1;	//32 ms
				Wheel1_Value = 0;
				timer2_init();
				ints_enable(0);
				if (TCCR0 == 0) timer0_init();
				while (0 != EIMSK && Wheel_waits != 0);
				Wheel_waits = 1;
				Wheel2_Value = 0;
				timer2_init();
				ints_enable(1);
				if (TCCR0 == 0) timer0_init();
				while (0 != EIMSK && Wheel_waits != 0);
				Wheel_waits = 1;
				Wheel3_Value = 0;
				timer2_init();
				ints_enable(2);
				if (TCCR0 == 0) timer0_init();
				while (0 != EIMSK && Wheel_waits != 0);
				Wheel_waits = 1;
				Wheel4_Value = 0;
				timer2_init();
				ints_enable(3);
				if (TCCR0 == 0) timer0_init();
				while (0 != EIMSK && Wheel_waits != 0);
			
				TIMSK &= ~(1<<TOIE2);
				EIMSK = 0;
			
				counter = 0;
				tx_flag = 1; // next parse() call will print data to serial port.
			
			break;
			
			case 1:		//setInteriorLightsOn
				PWM5_set_level (1);
				car_data[9] = 1;
				
				SendStr("done1");
			break;
			case 2:		//setInteriorLightsOff
				PWM5_set_level (0);
				car_data[9] = 0;
			break;
			case 3:		//activateHighBeam
				PWM2_set_level (1);
				//SendStr("done3");
			break;
			case 4:		//deactivateHighBeam
				PWM2_set_level (0);
				//SendStr("done4");
			break;
			case 5:		//activateLowBeam
				PWM1_set_level (1);
				//SendStr("done5");
			break;
			case 6:		//deactivateLowBeam
				PWM1_set_level (0);
			
			break;
			case 7:		//activateFutureUse
				PWM6_set_level (1);
	
			break;
			case 8:		//deactivateFutureUse
				PWM6_set_level (0);
	
			break;
			case 9:		//activateLeftWindowUp
				LW_HIGH_set_level(1);
				LW_LOW_set_level(0);
				LW_INH_set_level(1);
				car_data[12] = 1;
				LW_periods = 62;	// 1s
				if (TCCR0 == 0) timer0_init();
			break;
			case 10:		//activateLeftWindowDown
				LW_HIGH_set_level(0);
				LW_LOW_set_level(1);
				LW_INH_set_level(1);
				car_data[13] = 1;
				LW_periods = 62;	// 1s
				if (TCCR0 == 0) timer0_init();
			break;
			case 11:		//activateRightWindowUp
				RW_HIGH_set_level(1);
				RW_LOW_set_level(0);
				RW_INH_set_level(1);
				car_data[14] = 1;
				RW_periods = 62;	// 1s
				if (TCCR0 == 0) timer0_init();
			break;
			case 12:	//activateRightWindowDown
				RW_HIGH_set_level(0);
				RW_LOW_set_level(1);
				RW_INH_set_level(1);
				car_data[15] = 1;
				RW_periods = 62;	// 1s
				if (TCCR0 == 0) timer0_init();
			break;
			case 13:	//activateWindshieldWiper1
				if (DRV11_get_level() == 0) {
					DRV11_set_level(1);
					car_data[10] = car_data[10] + 100;
				} 
			break;
			case 14:	//activateWindshieldWiper2
				if (DRV12_get_level() == 0) {
					DRV12_set_level(1);
					car_data[10] = car_data[10] + 20;
				}
			break;
			case 15:	//activateWindshieldWiper3
				if (DRV13_get_level() == 0) {
					DRV13_set_level(1);
					car_data[10] = car_data[10] + 3;
				}
			break;
			case 16:	//deactivateWindshieldWiper
				DRV11_set_level(0);
				DRV12_set_level(0);
				DRV13_set_level(0);
				car_data[10] = 0;
			break;
			case 17:	//activateDoorLatch1
				DRV31_set_level(1);
				L1_periods = 31;	//500 ms
				if (TCCR0 == 0) timer0_init();
			break;
			case 18:	//activateDoorLatch2
				DRV32_set_level(1);
				L2_periods = 31;	//500 ms
				if (TCCR0 == 0) timer0_init();
			break;
			case 19:	//activateDoorLatch3
				DRV33_set_level(1);
				L3_periods = 31;	//500 ms
				if (TCCR0 == 0) timer0_init();
			break;
			case 20:	//activateDoorLatch4
				DRV34_set_level(1);
				L4_periods = 31;	//500 ms
				if (TCCR0 == 0) timer0_init();
			break;
			default:
			
				SendStr("default");
				
			    digits=0;
				for (unsigned char i = 0; i < _rx_buffer_tail; i++){
					if (_rx_buffer[i] >= 0x30 && _rx_buffer[i] <= 0x39){
						digits = digits + (_rx_buffer[i] & 0x0F);
						digits = digits * 10;
						_rx_buffer[i] = 0;
					};
				};
				digits = digits / 10;
				nums = 21;
				while (0 != strcmp(_rx_buffer, strcpy_P(queue, (PGM_P)pgm_read_word(&(InputStringPointers[nums]))))) {
					nums++;
					if (nums > 23) break;
				};
				
			
				if (nums == 21) {		// setLightsLevel
					if (digits >= 100){
						PWM3_set_level(1);
						TCCR1A &= ~(1<<COM1C1);		// PWM3 OUT disable
					}
					else if (digits == 0) {
						PWM3_set_level(0);
						TCCR1A &= ~(1<<COM1C1);		// PWM3 OUT disable
					}
					else {
						PWM3_Value = digits * 10;
						if (TCCR1A & (1<<COM1C1)) {
							TIMSK |= (1<<TOIE1);	// Enable overflow interrupt TIMER1
						}
						else {
							OCR1C = PWM3_Value;
							TCCR1A |= (1<<COM1C1);	// PWM3 OUT enable
						}
					}
				}
				else if	(nums == 22) {	// setVentsLevel
					car_data[11] = digits;
					if (car_data[11] >= 100){
						PWM4_set_level(1);
						TCCR3A &= ~(1<<COM3A1);		// PWM4 OUT disable
					}
					else if (car_data[11] == 0) {
						PWM4_set_level(0);
						TCCR3A &= ~(1<<COM3A1);		// PWM4 OUT disable
					}
					else {
						PWM4_Value = car_data[11] * 10;
						if (TCCR3A & (1<<COM3A1)) {
							ETIMSK |= (1<<TOIE3);	// Enable overflow interrupt TIMER3
						}
						else {
							OCR3A = PWM4_Value;
							TCCR3A |= (1<<COM3A1);	// PWM4 OUT enable
						}
					}
					
					
				}
				else if	(nums == 23) {	//setSteeringAssist
					car_data[18] = digits;
					MCP41x_Set_Vlaue((unsigned long) car_data[18]*1000,1); //MCP41x_Set_Vlaue(the desired value in ohm, the concerned side  RA or RB)
				}
		}
		memset(_rx_buffer, 0, _rx_buffer_tail);
		_rx_buffer_head = 0;
		_rx_buffer_tail = 0;
	}
	else if (tx_flag && Wheel_waits == 0) {
			tx_flag = 0;
			if (counter != 29) { // 19    - count what ?
				if (counter == 1) {
					dtostrf((float)Wheel1_Value * 0.025, 3, 3, buffer_digits); //wheel1
				}
				else if (counter == 2) {
					dtostrf((float)Wheel2_Value * 0.025, 3, 3, buffer_digits); //wheel2
				}
				else if (counter == 3) {
					dtostrf((float)Wheel3_Value * 0.025, 3, 3, buffer_digits); //wheel3
				}
				else if (counter == 4) {
					dtostrf((float)Wheel4_Value * 0.025, 3, 3, buffer_digits); //wheel4
				}
				else if (counter == 16) {
					dtostrf((float)read_adc_volt(5), 3, 1, buffer_digits);	// battery  (is battery)
				}
				else if (counter == 17) {
					dtostrf((float)read_adc(6), 3, 1, buffer_digits); // steering wheel position
				}
				
				else if(counter == 20){ // 
					dtostrf((float)read_adc(0), 3, 1, buffer_digits); // ADC1 (works) 
				}
				else if(counter == 21){ //
					dtostrf((float)read_adc(1), 3, 1, buffer_digits); // ADC2 (works)
				}
				else if(counter == 22){ //
					dtostrf((float)read_adc(2), 3, 1, buffer_digits); // ADC3 (Door 3)
				}
				else if(counter == 23){ //
					dtostrf((float)read_adc(3), 3, 1, buffer_digits); // ADC4 (Door 4)
				}
				else if(counter == 24){ //
					dtostrf((float)read_adc(4), 3, 1, buffer_digits); // ADC5 (sense11 hazard)
				}
				else if(counter == 25){ //
					dtostrf((float)read_adc(5), 3, 1, buffer_digits); // ADC6 (battery)
				}
				else if(counter == 26){ //
					dtostrf((float)read_adc(6), 3, 1, buffer_digits); // ADC7  (works)
				}
				else if(counter == 27){ //
					dtostrf((float)read_adc(7), 3, 1, buffer_digits); // ADC8  (works)
				}
				else {
					utoa(car_data[counter], buffer_digits, 10);	
				}
				
				//SendStr("\n");
				SendStr(strcpy_P(_tx_buffer, (PGM_P)pgm_read_word(&(VariablePointers[counter])))); // Print variable status to serial
				counter++;
			}
	}

}