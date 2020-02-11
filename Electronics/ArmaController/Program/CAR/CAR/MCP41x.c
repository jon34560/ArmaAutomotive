/*
 * MCP41x.c
 *
 * Created: 10.04.2019 1:53:06
 *  Author: RUSLAN
 */ 

#include <atmel_start.h>

unsigned long  MCP41x_Max_Value_Set=0;
char wipper_resistance_=125;

void SPI_Init(void)
{
// SPI Type: Master
// SPI Clock Rate: 1000.000 kHz
// SPI Clock Phase: Cycle Start
// SPI Clock Polarity: Low
// SPI Data Order: MSB First
    SPCR=0x50;
    SPSR=0x00;
}


void SPI_WriteByte(uint8_t data)
{
	SPDR = data;
	while(!(SPSR & (1<<SPIF)));
}



void MCP41x_Init(unsigned long  MCP41x_Max_Value, char _wipper_resistance){
	MCP41x_Max_Value_Set=MCP41x_Max_Value;
	wipper_resistance_=_wipper_resistance;
	SPI_Init();

}


char MCP41x_Set_Vlaue(unsigned long MCP41x_Resistance_Value,char MCP41x_RAB){
	long calcule_MCP41x = 0;
	if(MCP41x_Resistance_Value>MCP41x_Max_Value_Set)  return 0;
	calcule_MCP41x = (long)MCP41x_Resistance_Value - wipper_resistance_;
	if(calcule_MCP41x <= 0){return 52;}
	calcule_MCP41x = calcule_MCP41x*256;
	calcule_MCP41x = calcule_MCP41x / MCP41x_Max_Value_Set;
	calcule_MCP41x--;
	CS_set_level(0);
	SPI_WriteByte(0b00010001);
	
	if(MCP41x_RAB == 0) calcule_MCP41x = 256 - calcule_MCP41x;
	SPI_WriteByte((char)calcule_MCP41x);
	CS_set_level(1);
	return (char)calcule_MCP41x;	
}