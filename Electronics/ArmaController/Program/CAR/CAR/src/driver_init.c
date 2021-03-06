/**
 * \file
 *
 * \brief Driver initialization.
 *
 (c) 2018 Microchip Technology Inc. and its subsidiaries.

    Subject to your compliance with these terms,you may use this software and
    any derivatives exclusively with Microchip products.It is your responsibility
    to comply with third party license terms applicable to your use of third party
    software (including open source software) that may accompany Microchip software.

    THIS SOFTWARE IS SUPPLIED BY MICROCHIP "AS IS". NO WARRANTIES, WHETHER
    EXPRESS, IMPLIED OR STATUTORY, APPLY TO THIS SOFTWARE, INCLUDING ANY IMPLIED
    WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY, AND FITNESS FOR A
    PARTICULAR PURPOSE.

    IN NO EVENT WILL MICROCHIP BE LIABLE FOR ANY INDIRECT, SPECIAL, PUNITIVE,
    INCIDENTAL OR CONSEQUENTIAL LOSS, DAMAGE, COST OR EXPENSE OF ANY KIND
    WHATSOEVER RELATED TO THE SOFTWARE, HOWEVER CAUSED, EVEN IF MICROCHIP HAS
    BEEN ADVISED OF THE POSSIBILITY OR THE DAMAGES ARE FORESEEABLE. TO THE
    FULLEST EXTENT ALLOWED BY LAW, MICROCHIP'S TOTAL LIABILITY ON ALL CLAIMS IN
    ANY WAY RELATED TO THIS SOFTWARE WILL NOT EXCEED THE AMOUNT OF FEES, IF ANY,
    THAT YOU HAVE PAID DIRECTLY TO MICROCHIP FOR THIS SOFTWARE.
 *
 */

/*
 * Code generated by START.
 *
 * This file will be overwritten when reconfiguring your START project.
 * Please copy examples or other code you want to keep to a separate file
 * to avoid losing it when reconfiguring.
 */

#include "driver_init.h"
#include <system.h>

/**
 * \brief System initialization
 */
void system_init()
{
	mcu_init();

	/* PORT setting on PA0 */

	// Set pin direction to input
	SENS1_set_dir(PORT_DIR_IN);

	SENS1_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PA1 */

	// Set pin direction to input
	SENS2_set_dir(PORT_DIR_IN);

	SENS2_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PA2 */

	// Set pin direction to input
	SENS3_set_dir(PORT_DIR_IN);

	SENS3_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PA3 */

	// Set pin direction to input
	SENS4_set_dir(PORT_DIR_IN);

	SENS4_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PA4 */

	// Set pin direction to input
	SENS5_set_dir(PORT_DIR_IN);

	SENS5_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PA5 */

	// Set pin direction to input
	SENS6_set_dir(PORT_DIR_IN);

	SENS6_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PA6 */

	// Set pin direction to input
	SENS7_set_dir(PORT_DIR_IN);

	SENS7_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PA7 */

	// Set pin direction to input
	SENS8_set_dir(PORT_DIR_IN);

	SENS8_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);
		
		
		
	/* PORT setting on PF5 */	
		
	// Set pin direction to input
	SENS11_set_dir(PORT_DIR_IN);

	SENS11_set_pull_mode(
	// <y> Pull configuration
	// <id> pad_pull_config
	// <PORT_PULL_OFF"> Off
	// <PORT_PULL_UP"> Pull-up
	PORT_PULL_OFF);
		

	/* PORT setting on PB0 */

	// Set pin direction to output
	SS_set_dir(PORT_DIR_OUT);

	SS_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    true);

	/* PORT setting on PB1 */

	// Set pin direction to output
	SCK_set_dir(PORT_DIR_OUT);

	SCK_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    true);

	/* PORT setting on PB2 */

	// Set pin direction to output
	MOSI_set_dir(PORT_DIR_OUT);

	MOSI_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    true);

	/* PORT setting on PB3 */

	// Set pin direction to input
	MISO_set_dir(PORT_DIR_IN);

	MISO_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_UP);

	/* PORT setting on PB4 */

	// Set pin direction to output
	LT_set_dir(PORT_DIR_OUT);

	LT_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PB5 */

	// Set pin direction to output
	PWM1_set_dir(PORT_DIR_OUT);

	PWM1_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PB6 */

	// Set pin direction to output
	PWM2_set_dir(PORT_DIR_OUT);

	PWM2_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PB7 */

	// Set pin direction to output
	PWM3_set_dir(PORT_DIR_OUT);

	PWM3_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PC0 */

	// Set pin direction to output
	DRV11_set_dir(PORT_DIR_OUT);

	DRV11_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PC1 */

	// Set pin direction to output
	DRV12_set_dir(PORT_DIR_OUT);

	DRV12_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PC2 */

	// Set pin direction to output
	DRV13_set_dir(PORT_DIR_OUT);

	DRV13_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PC3 */

	// Set pin direction to output
	DRV31_set_dir(PORT_DIR_OUT);

	DRV31_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PC4 */

	// Set pin direction to output
	DRV32_set_dir(PORT_DIR_OUT);

	DRV32_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PC5 */

	// Set pin direction to output
	DRV33_set_dir(PORT_DIR_OUT);

	DRV33_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PC6 */

	// Set pin direction to output
	DRV34_set_dir(PORT_DIR_OUT);

	DRV34_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PC7 */

	// Set pin direction to output
	DRV4_set_dir(PORT_DIR_OUT);

	DRV4_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PD0 */

	// Set pin direction to input
	HALL1_set_dir(PORT_DIR_IN);

	HALL1_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PD1 */

	// Set pin direction to input
	HALL2_set_dir(PORT_DIR_IN);

	HALL2_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PD2 */

	// Set pin direction to input
	HALL3_set_dir(PORT_DIR_IN);

	HALL3_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PD3 */

	// Set pin direction to input
	HALL4_set_dir(PORT_DIR_IN);

	HALL4_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PD4 */

	// Set pin direction to output
	LW_INH_set_dir(PORT_DIR_OUT);

	LW_INH_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PD5 */

	// Set pin direction to output
	RT_set_dir(PORT_DIR_OUT);

	RT_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PD6 */

	// Set pin direction to output
	RD_set_dir(PORT_DIR_OUT);

	RD_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PD7 */

	// Set pin direction to output
	RB_set_dir(PORT_DIR_OUT);

	RB_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PE0 */

	/* PORT setting on PE1 */

	/* PORT setting on PE2 */

	// Set pin direction to output
	CS_set_dir(PORT_DIR_OUT);

	CS_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    true);

	/* PORT setting on PE3 */

	// Set pin direction to output
	PWM4_set_dir(PORT_DIR_OUT);

	PWM4_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PE4 */

	// Set pin direction to output
	PWM5_set_dir(PORT_DIR_OUT);

	PWM5_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PE5 */

	// Set pin direction to output
	PWM6_set_dir(PORT_DIR_OUT);

	PWM6_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PE6 */

	// Set pin direction to output
	DRV5_set_dir(PORT_DIR_OUT);

	DRV5_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PE7 */

	// Set pin direction to output
	DRV6_set_dir(PORT_DIR_OUT);

	DRV6_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PF0 */

	// Disable pull-up.
	ADC_CH1_set_pull_mode(PORT_PULL_OFF);

	/* PORT setting on PF1 */

	// Disable pull-up.
	ADC_CH2_set_pull_mode(PORT_PULL_OFF);

	/* PORT setting on PF2 */

	// Set pin direction to input
	SENS9_set_dir(PORT_DIR_IN);

	SENS9_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PF3 */

	// Set pin direction to input
	SENS10_set_dir(PORT_DIR_IN);

	SENS10_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PF4 */

	// Set pin direction to input
	SENS11_set_dir(PORT_DIR_IN);

	SENS11_set_pull_mode(
	    // <y> Pull configuration
	    // <id> pad_pull_config
	    // <PORT_PULL_OFF"> Off
	    // <PORT_PULL_UP"> Pull-up
	    PORT_PULL_OFF);

	/* PORT setting on PF5 */

	// Disable pull-up.
	ADC_POW_set_pull_mode(PORT_PULL_OFF);

	/* PORT setting on PF6 */

	// Disable pull-up.
	ADC6_set_pull_mode(PORT_PULL_OFF);

	/* PORT setting on PF7 */

	// Disable pull-up.
	ADC7_set_pull_mode(PORT_PULL_OFF);

	/* PORT setting on PG0 */

	// Set pin direction to output
	LW_HIGH_set_dir(PORT_DIR_OUT);

	LW_HIGH_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PG1 */

	// Set pin direction to output
	LW_LOW_set_dir(PORT_DIR_OUT);

	LW_LOW_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PG2 */

	// Set pin direction to output
	RW_HIGH_set_dir(PORT_DIR_OUT);

	RW_HIGH_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PG3 */

	// Set pin direction to output
	RW_LOW_set_dir(PORT_DIR_OUT);

	RW_LOW_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	/* PORT setting on PG4 */

	// Set pin direction to output
	RW_INH_set_dir(PORT_DIR_OUT);

	RW_INH_set_level(
	    // <y> Initial level
	    // <id> pad_initial_level
	    // <false"> Low
	    // <true"> High
	    false);

	sysctrl_init();
}
