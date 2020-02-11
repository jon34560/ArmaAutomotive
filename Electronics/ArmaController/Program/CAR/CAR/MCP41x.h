/*
 * MCP41x.h
 *
 * Created: 10.04.2019 1:53:20
 *  Author: RUSLAN
 */ 


#ifndef MCP41X_H_
#define MCP41X_H_

void SPI_Init(void);
void MCP41x_Init(unsigned long  MCP41x_Max_Value, char _wipper_resistance);
char MCP41x_Set_Vlaue(unsigned long MCP41x_Resistance_Value,char MCP41x_RAB);


#endif /* MCP41X_H_ */