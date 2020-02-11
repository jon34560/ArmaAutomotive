/*
 * adc.c
 *
 * Created: 07.04.2019 14:45:27
 *  Author: RUSLAN
 */ 


#include <atmel_start.h>
#include <util/delay.h>

////////////////ADC0 channel input initialization//////////////
//
// Config. the ADC to take input from channel 0:
// * Reference: Aref
// * Channel: 0
// * ADC clock: 16MHz/128 = 125KHz
// * Mode: single conversion
// * Result justification: right adjusted
//
//////////////////////////////////////////////////////////////


void ADC_Init(void)
{
	//ADEN: ADC Enable
	//ADSC: ADC Start Conversion
	//ADIE: ADC Interrupt Enable
	//ADPS2:0: ADC Prescaller Select Bits (Division Factor=128)
	//ADMUX |=(1<<REFS0); //AVCC — ref		
	ADCSRA = (1<<ADEN)|(1<<ADPS2)|(1<<ADPS1)|(1<<ADPS0);
}


unsigned int read_adc(unsigned char adc_input)
{
	
	ADMUX = adc_input | (1<<REFS0);
	// Delay needed for the stabilization of the ADC input voltage
	_delay_us(10);
	_delay_us(2);
	// Start the AD conversion
	ADCSRA|=(1<<ADSC);
	// Wait for the AD conversion to complete
	while ((ADCSRA & (1<<ADIF))==0);
	ADCSRA|=(1<<ADIF);
	return ADCW;
	
	//return 0;
}

// Read the AD conversion result
float read_adc_volt(unsigned char adc_input)
{
	float out_adc = (float) read_adc(adc_input) * 0.0048828125 * 4.745;
	return out_adc;
}