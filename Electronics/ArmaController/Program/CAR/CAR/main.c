/**
* Arma Automotive
* Controller Firmware
* 
* Bugs: serial get command resets turn signals.
*/
#include <atmel_start.h>

//#include <avrlibdefs.h>
#include <avrlibtypes.h>
#include <util/delay.h>
//#include <defines.h>
#include <serial.h>
#include <serial_communication.h>
#include <timers.h>
#include <adc.h>
#include <MCP41x.h>

#define  pulse_time_code 188 //pulse_time_code = pulse_time (ms) / 0.025 for Steering power assist

int main(void)
{
/* Initializes MCU, drivers and middleware */
	unsigned char left_turn_mem = 0;
	unsigned char right_turn_mem = 0;
	uint16_t delay_meas = 30000;
	unsigned char control_Steering = 1;
	
	atmel_start_init();
	PWM3_set_level(1);	//setLightsLevel 100%
	ADC_Init();
	serial_init();
	timers_init();
	MCP41x_Init(100000,125);  //MCP41x_Init(max value of digital pot in ohm "10k, 50 k, 100 k" , RW "Wiper Resistance" for the value in ohm see datasheet )
	MCP41x_Set_Vlaue(50000,1); //MCP41x_Set_Vlaue(the desired value in ohm, the concerned side  RA or RB)
	car_data[0] = 1;	//car_on
	PWM1_set_level(1);	//low beam light output should turn on on startup
	PWM3_set_level(1);	//setLightsLevel 100%
	
	// TEST
	//PORTE_set_pin_dir(5, PORT_DIR_OUT);
	//PORTE_set_pin_dir(6, PORT_DIR_OUT);
	//PORTE_set_pin_level(5, true);
	//PORTE_set_pin_level(6, true);
	
	//SENS11_set_dir(PORT_DIR_IN); // hazard button io line.
	
	sei();
	
	//SendStr("ArmaGT Controller. v1.1");
	
	while (1) {
		parcer();
		
		if (SENS1_get_level() == 0 && LT_delay == 0) { // Left turn signal
			LT_delay = 18; //288 ms
			if (TCCR0 == 0) timer0_init();
			
			if (car_data[7]) {
				car_data[7] = 0;
				LT_set_level (0);
				LRT_periods = 0;
				left_turn_mem = 0;
			}
			else {
				car_data[7] = 1;
				RT_set_level (0);
				LT_set_level (1);
				car_data[8] = 0;
				LRT_periods = 15;
			}
		}
		else if (SENS2_get_level() == 0 && RT_delay == 0) { // Right turn signal
			RT_delay = 18; //288 ms
			if (TCCR0 == 0) timer0_init();
			
			if (car_data[8]) {
				car_data[8] = 0;
				RT_set_level (0);
				LRT_periods = 0;
				right_turn_mem = 0;
			}
			else {
				car_data[8] = 1;
				LT_set_level (0);
				RT_set_level (1);
				car_data[7] = 0;
				LRT_periods = 15;
			}
		}
		
		if (SENS3_get_level() == 0 && RB_get_level() == 0) {	// Brake ON
			RB_set_level(1);
			car_data[5] = 1;
		}
		else if (SENS3_get_level() && RB_get_level()) {			// Brake OFF
			RB_set_level(0);
			car_data[5] = 0;
		}
		
		// Enable horn if button pressed and the horn is currently off.
		// (pa3 PORTA_get_pin_level(3), PORTC_get_pin_level(7) pc7  )
		if (SENS4_get_level() == 0 && DRV4_get_level() == 0) {	// Horn ON   
			DRV4_set_level(1);
			car_data[19] = 1;
			
			// debug
			//PORTE_set_pin_level(5, true);
			//PORTE_set_pin_level(6, true);
		//	DRV6_set_level(1); // J8 pin 11
			//DRV5_set_level(1);
		}
		else if (SENS4_get_level() && DRV4_get_level()) {		// Horn OFF
			DRV4_set_level(0);
			car_data[19] = 0;
			
			// debug
			//PORTE_set_pin_level(5, false);
			//PORTE_set_pin_level(6, false);
		//	DRV6_set_level(0); // J8 pin 11
			//DRV5_set_level(0);
		}
		
		if (SENS5_get_level() == 0 && beam_delay == 0) {		// high-beam
			if (PWM2_get_level()) PWM2_set_level (0); else PWM2_set_level (1);
			beam_delay = 18; //288 ms
			if (TCCR0 == 0) timer0_init();
		}
		//else if (SENS6_get_level()) {	//Right wight
			
		//}
		
		// Power windows
		if (SENS7_get_level() == 0 && SENS8_get_level() && LW_INH_get_level() == 0) {	// LW (Left Window) UP ON
			if (SENS6_get_level() == 0) {	//Right wight
				DRV31_set_level(1);		//activateDoorLatch1
				L1_periods = 31;		//500 ms
				if (TCCR0 == 0) timer0_init();
			}
			else {
				LW_HIGH_set_level(1);
				LW_LOW_set_level(0);
				LW_INH_set_level(1);
				car_data[12] = 1;	
			}
		}
		else if (SENS7_get_level() && LW_HIGH_get_level() && LW_periods == 0) {	//LW UP OFF
			LW_HIGH_set_level(0);
			LW_LOW_set_level(0);
			LW_INH_set_level(0);
			car_data[12] = 0;
		}
		else if (SENS8_get_level() == 0 && SENS7_get_level() && LW_INH_get_level() == 0) {	//LW DOWN ON
			if (SENS6_get_level() == 0){		//Right wight
				DRV32_set_level(1);		//activateDoorLatch2
				L2_periods = 31;		//500 ms
				if (TCCR0 == 0) timer0_init();
			}
			else{
				LW_HIGH_set_level(0);
				LW_LOW_set_level(1);
				LW_INH_set_level(1);
				car_data[13] = 1;	
			}
		}
		else if (SENS8_get_level() && LW_LOW_get_level() && LW_periods == 0) {	//LW DOWN OFF
			LW_HIGH_set_level(0);
			LW_LOW_set_level(0);
			LW_INH_set_level(0);
			car_data[13] = 0;
		}
		if (SENS9_get_level() == 0 && SENS10_get_level() && RW_INH_get_level() == 0) {	//RW UP ON
			if (SENS6_get_level() == 0){		//Right wight
				DRV33_set_level(1);		//activateDoorLatch3
				L3_periods = 31;		//500 ms
				if (TCCR0 == 0) timer0_init();
			}
			else{
				RW_HIGH_set_level(1);
				RW_LOW_set_level(0);
				RW_INH_set_level(1);
				car_data[14] = 1;	
			}
		}
		else if (SENS9_get_level() && RW_HIGH_get_level() && RW_periods == 0) {	// RW UP OFF
			RW_HIGH_set_level(0);
			RW_LOW_set_level(0);
			RW_INH_set_level(0);
			car_data[14] = 1;
		}
		else if (SENS10_get_level() == 0 && SENS9_get_level() && RW_INH_get_level() == 0) {	//RW DOWN ON
			if (SENS6_get_level() == 0){		//Right wight
				DRV34_set_level(1);		//activateDoorLatch4
				L4_periods = 31;		//500 ms
				if (TCCR0 == 0) timer0_init();
			}
			else {
				RW_HIGH_set_level(0);
				RW_LOW_set_level(1);
				RW_INH_set_level(1);
				car_data[15] = 1;	
			}
		}
		else if (SENS10_get_level() && RW_LOW_get_level() && RW_periods == 0) {	//RW DOWN OFF
			RW_HIGH_set_level(0);
			RW_LOW_set_level(0);
			RW_INH_set_level(0);
			car_data[15] = 0;
		}
		
		// Hazard button
		if (SENS11_get_level() == 0 && Hz_delay == 0) { // Hazard (toggle? because i don't want toggle )
			/*
			Hz_delay = 18; //288 ms
			if (TCCR0 == 0) timer0_init();
			
			if (car_data[6]) {
				car_data[6] = 0;			// THIS WON'T WORK. don't want to toggle the status of the hazard while active.
				LT_set_level (0);
				RT_set_level (0);
				RD_set_level (0); //rear daytime lights
				LRT_periods = 0;
			}
			else {
				car_data[6] = 1;
				LT_set_level (1);
				RT_set_level (1);
				RD_set_level (1); //rear daytime lights
				LRT_periods = 15;
			}
			*/
			//RT_toggle_level();
			//_delay_ms(300);
		}
		
		// Hazard light buttons
		// SENS11_get_level is PF4 (ADC4)  which may be interferring
		if(SENS11_get_level() == 0){
		//	car_data[6] = 1;
			
			// Debug with aug IO line
			//DRV6_set_level(1); // J8 pin 11
			
		} else if(SENS11_get_level()){
		//	car_data[6] = 0;
			
			// Debug with aug IO line
			//DRV6_set_level(0); // J8 pin 11
		}
		
		int adc4 = read_adc(4);
		if(adc4 < 150){
			car_data[6] = 1;	// set hazard flag on 
			
			//LT_set_level (0);
			//RT_set_level (0);
			//RD_set_level (0); //rear daytime lights
			//LRT_periods = 0;
			
			DRV6_set_level(1); // J8 pin 11
		} else {
			car_data[6] = 0;	// set hazard flag off
			
			//LT_set_level (1);
			//RT_set_level (1);
			//RD_set_level (1); //rear daytime lights
			//LRT_periods = 15;
			
			DRV6_set_level(0); // J8 pin 11
		}
		
		/*	
		int adc0 = read_adc(0);	
		if(adc0 > 200){
			//DRV6_set_level(1); // J8 pin 11
		}{
			//DRV6_set_level(0); // J8 pin 11
		}
		*/
				
		// ?			
		if (LRT_periods == 0){
			
			if (car_data[6] ) {			//hazard
				if (LT_get_level()) LT_set_level (0); else LT_set_level (1);
				if (RT_get_level()) RT_set_level (0); else RT_set_level (1);
				LRT_periods = 15;
				if (TCCR0 == 0) timer0_init();
			}
			else 
			
			if (car_data[7] ) {	//left_turn_signal
				if (left_turn_mem){
					if (read_adc(6) >= 512) {
						car_data[7] = 0;
						LT_set_level (0);
						left_turn_mem = 0;
					}
					else {
						if (LT_get_level()) LT_set_level (0); else LT_set_level (1);
						LRT_periods = 15;
						if (TCCR0 == 0) timer0_init();
					}
				}
				else {
					if (read_adc(6) < 512) {
						left_turn_mem = 1;
					}
					else {
						if (LT_get_level()) LT_set_level (0); else LT_set_level (1);
						LRT_periods = 15;
						if (TCCR0 == 0) timer0_init();
					}
				}
			}
			else if (car_data[8] ) {	//right_turn_signal
				if (right_turn_mem){
					if (read_adc(6) <= 512) {
						car_data[8] = 0;
						RT_set_level (0);
						right_turn_mem = 0;
					}
					else {
						if (RT_get_level()) RT_set_level (0); else RT_set_level (1);
						LRT_periods = 15;
						if (TCCR0 == 0) timer0_init();
					}
				}
				else {
					if (read_adc(6) > 512) {
						right_turn_mem = 1;
					}
					else {
						if (RT_get_level()) RT_set_level (0); else RT_set_level (1);
						LRT_periods = 15;
						if (TCCR0 == 0) timer0_init();
					}
				}
			}
		}
		
		if (L1_periods == 0 && DRV31_get_level()){
			DRV31_set_level(0);
		}
		if (L2_periods == 0 && DRV32_get_level()){
			DRV32_set_level(0);
		}
		if (L3_periods == 0 && DRV33_get_level()){
			DRV33_set_level(0);
		}
		if (L4_periods == 0 && DRV34_get_level()){
			DRV34_set_level(0);
		}
	
		if (delay_meas == 0){
			//LT_toggle_level (); //activateFutureUse
			if ((read_adc(7) <= 205) && (DRV11_get_level() || DRV12_get_level()|| DRV13_get_level())) { // Windshield Wiper 0-1v off
				DRV11_set_level(0);
				DRV12_set_level(0);
				DRV13_set_level(0);
				car_data[10] = 0;
			}
			else {
				if (read_adc(7) > 205 && read_adc(7) < 410 && DRV11_get_level() == 0) {			// Windshield Wiper 1 1-2v
					DRV11_set_level(1);
					car_data[10] = car_data[10] + 100;
				}
				else if (read_adc(7) > 614 && read_adc(7) < 819 && DRV12_get_level() == 0) {	// Windshield Wiper 2 3-4v
					DRV12_set_level(1);
					car_data[10] = car_data[10] + 20;
				}
				else if (read_adc(7) > 819 && DRV13_get_level() == 0) {							// Windshield Wiper 3 4-5v
					DRV13_set_level(1);
					car_data[10] = car_data[10] + 3;
				}
			}
			if (control_Steering){
				Wheel_waits = 1;	//32 ms
				timer2_init();
				ints_enable(0);
				if (TCCR0 == 0) timer0_init();
				while (0 != EIMSK && Wheel_waits != 0);
				TIMSK &= ~(1<<TOIE2);
				EIMSK = 0;
				if (Wheel1_Value!=0 && Wheel1_Value <= pulse_time_code){
					Wheel1_Value = 0;
					control_Steering = 0;
					MCP41x_Set_Vlaue(10000,1); //MCP41x_Set_Vlaue(the desired value in ohm, the concerned side  RA or RB)
				}
			}
			delay_meas = 30000;
		} else {
			delay_meas--;
		}
	
	
	}
	
	
}
