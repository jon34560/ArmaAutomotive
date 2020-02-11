/*
 * adc.h
 *
 * Created: 07.04.2019 14:45:51
 *  Author: RUSLAN
 */ 


#ifndef ADC_H_
#define ADC_H_


void ADC_Init();
unsigned int read_adc(unsigned char adc_input);
float read_adc_volt(unsigned char adc_input);

#endif /* ADC_H_ */