/*
 * Defines.h
 *
 * Created: 28.03.2019 2:01:40
 *  Author: RUSLAN
 */ 


#ifndef DEFINES_H_
#define DEFINES_H_
const char Variable0[] PROGMEM = "car_on:";
const char Variable1[] PROGMEM = "wheel1:";
const char Variable2[] PROGMEM = "wheel2:";
const char Variable3[] PROGMEM = "wheel3:";
const char Variable4[] PROGMEM = "wheel4:";
const char Variable5[] PROGMEM = "brake:";
const char Variable6[] PROGMEM = "hazard:";
const char Variable7[] PROGMEM = "left_turn_signal:";
const char Variable8[] PROGMEM = "right_turn_signal:";
const char Variable9[] PROGMEM = "interior_lights:";
const char Variable10[] PROGMEM = "windshield_wiper:";
const char Variable11[] PROGMEM = "vents:";
const char Variable12[] PROGMEM = "power_windshieldLU:";
const char Variable13[] PROGMEM = "power_windshieldLD:";
const char Variable14[] PROGMEM = "power_windshieldRU:";
const char Variable15[] PROGMEM = "power_windshieldRD:";
const char Variable16[] PROGMEM = "battery:";
const char Variable17[] PROGMEM = "steering_wheel_position:";
const char Variable18[] PROGMEM = "steering_assist:";
const char Variable19[] PROGMEM = "horn:";
const char Variable20[] PROGMEM = "ADC1:";
const char Variable21[] PROGMEM = "ADC2:";
const char Variable22[] PROGMEM = "ADC3:";
const char Variable23[] PROGMEM = "ADC4:";
const char Variable24[] PROGMEM = "ADC5:";
const char Variable25[] PROGMEM = "ADC6:";
const char Variable26[] PROGMEM = "ADC7:";
const char Variable27[] PROGMEM = "ADC8:";
PGM_P const VariablePointers[] PROGMEM = {Variable0, Variable1, Variable2, Variable3, Variable4, Variable5, Variable6, Variable7, Variable8,
Variable9, Variable10, Variable11, Variable12, Variable13, Variable14, Variable15, Variable16, Variable17, Variable18, Variable19, Variable20,
Variable21, Variable22, Variable23, Variable24, Variable25, Variable26, Variable27};



const char InputString0[] PROGMEM = "get";
const char InputString1[] PROGMEM = "setInteriorLightsOn";
const char InputString2[] PROGMEM = "setInteriorLightsOff";
const char InputString3[] PROGMEM = "activateHighBeam";
const char InputString4[] PROGMEM = "deactivateHighBeam";
const char InputString5[] PROGMEM = "activateLowBeam";
const char InputString6[] PROGMEM = "deactivateLowBeam";
const char InputString7[] PROGMEM = "activateFutureUse";
const char InputString8[] PROGMEM = "deactivateFutureUse";
const char InputString9[] PROGMEM = "activateLeftWindowUp";
const char InputString10[] PROGMEM = "activateLeftWindowDown";
const char InputString11[] PROGMEM = "activateRightWindowUp";
const char InputString12[] PROGMEM = "activateRightWindowDown";
const char InputString13[] PROGMEM = "activateWindshieldWiper1";
const char InputString14[] PROGMEM = "activateWindshieldWiper2";
const char InputString15[] PROGMEM = "activateWindshieldWiper3";
const char InputString16[] PROGMEM = "deactivateWindshieldWiper";
const char InputString17[] PROGMEM = "activateDoorLatch1";
const char InputString18[] PROGMEM = "activateDoorLatch2";
const char InputString19[] PROGMEM = "activateDoorLatch3";
const char InputString20[] PROGMEM = "activateDoorLatch4";
const char InputString21[] PROGMEM = "setLightsLevel";
const char InputString22[] PROGMEM = "setVentsLevel";
const char InputString23[] PROGMEM = "setSteeringAssist";
// horn 
PGM_P const InputStringPointers[] PROGMEM = {InputString0, InputString1, InputString2, InputString3, InputString4, InputString5, InputString6, InputString7, InputString8,
InputString9, InputString10, InputString11, InputString12, InputString13, InputString14, InputString15, InputString16, InputString17, InputString18, InputString19, InputString20, InputString21, InputString22, InputString23};



#endif /* DEFINES_H_ */