/*
 * Code generated from Atmel Start.
 *
 * This file will be overwritten when reconfiguring your Atmel Start project.
 * Please copy examples or other code you want to keep to a separate file
 * to avoid losing it when reconfiguring.
 */
#ifndef ATMEL_START_PINS_H_INCLUDED
#define ATMEL_START_PINS_H_INCLUDED

#include <port.h>

/**
 * \brief Set SENS1 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SENS1_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTA_set_pin_pull_mode(0, pull_mode);
}

/**
 * \brief Set SENS1 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SENS1_set_dir(const enum port_dir dir)
{
	PORTA_set_pin_dir(0, dir);
}

/**
 * \brief Set SENS1 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SENS1_set_level(const bool level)
{
	PORTA_set_pin_level(0, level);
}

/**
 * \brief Toggle output level on SENS1
 *
 * Toggle the pin level
 */
static inline void SENS1_toggle_level()
{
	PORTA_toggle_pin_level(0);
}

/**
 * \brief Get level on SENS1
 *
 * Reads the level on a pin
 */
static inline bool SENS1_get_level()
{
	return PORTA_get_pin_level(0);
}

/**
 * \brief Set SENS2 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SENS2_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTA_set_pin_pull_mode(1, pull_mode);
}

/**
 * \brief Set SENS2 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SENS2_set_dir(const enum port_dir dir)
{
	PORTA_set_pin_dir(1, dir);
}

/**
 * \brief Set SENS2 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SENS2_set_level(const bool level)
{
	PORTA_set_pin_level(1, level);
}

/**
 * \brief Toggle output level on SENS2
 *
 * Toggle the pin level
 */
static inline void SENS2_toggle_level()
{
	PORTA_toggle_pin_level(1);
}

/**
 * \brief Get level on SENS2
 *
 * Reads the level on a pin
 */
static inline bool SENS2_get_level()
{
	return PORTA_get_pin_level(1);
}

/**
 * \brief Set SENS3 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SENS3_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTA_set_pin_pull_mode(2, pull_mode);
}

/**
 * \brief Set SENS3 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SENS3_set_dir(const enum port_dir dir)
{
	PORTA_set_pin_dir(2, dir);
}

/**
 * \brief Set SENS3 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SENS3_set_level(const bool level)
{
	PORTA_set_pin_level(2, level);
}

/**
 * \brief Toggle output level on SENS3
 *
 * Toggle the pin level
 */
static inline void SENS3_toggle_level()
{
	PORTA_toggle_pin_level(2);
}

/**
 * \brief Get level on SENS3
 *
 * Reads the level on a pin
 */
static inline bool SENS3_get_level()
{
	return PORTA_get_pin_level(2);
}

/**
 * \brief Set SENS4 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SENS4_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTA_set_pin_pull_mode(3, pull_mode);
}

/**
 * \brief Set SENS4 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SENS4_set_dir(const enum port_dir dir)
{
	PORTA_set_pin_dir(3, dir);
}

/**
 * \brief Set SENS4 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SENS4_set_level(const bool level)
{
	PORTA_set_pin_level(3, level);
}

/**
 * \brief Toggle output level on SENS4
 *
 * Toggle the pin level
 */
static inline void SENS4_toggle_level()
{
	PORTA_toggle_pin_level(3);
}

/**
 * \brief Get level on SENS4
 *
 * Reads the level on a pin
 */
static inline bool SENS4_get_level()
{
	return PORTA_get_pin_level(3);
}

/**
 * \brief Set SENS5 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SENS5_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTA_set_pin_pull_mode(4, pull_mode);
}

/**
 * \brief Set SENS5 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SENS5_set_dir(const enum port_dir dir)
{
	PORTA_set_pin_dir(4, dir);
}

/**
 * \brief Set SENS5 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SENS5_set_level(const bool level)
{
	PORTA_set_pin_level(4, level);
}

/**
 * \brief Toggle output level on SENS5
 *
 * Toggle the pin level
 */
static inline void SENS5_toggle_level()
{
	PORTA_toggle_pin_level(4);
}

/**
 * \brief Get level on SENS5
 *
 * Reads the level on a pin
 */
static inline bool SENS5_get_level()
{
	return PORTA_get_pin_level(4);
}

/**
 * \brief Set SENS6 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SENS6_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTA_set_pin_pull_mode(5, pull_mode);
}

/**
 * \brief Set SENS6 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SENS6_set_dir(const enum port_dir dir)
{
	PORTA_set_pin_dir(5, dir);
}

/**
 * \brief Set SENS6 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SENS6_set_level(const bool level)
{
	PORTA_set_pin_level(5, level);
}

/**
 * \brief Toggle output level on SENS6
 *
 * Toggle the pin level
 */
static inline void SENS6_toggle_level()
{
	PORTA_toggle_pin_level(5);
}

/**
 * \brief Get level on SENS6
 *
 * Reads the level on a pin
 */
static inline bool SENS6_get_level()
{
	return PORTA_get_pin_level(5);
}

/**
 * \brief Set SENS7 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SENS7_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTA_set_pin_pull_mode(6, pull_mode);
}

/**
 * \brief Set SENS7 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SENS7_set_dir(const enum port_dir dir)
{
	PORTA_set_pin_dir(6, dir);
}

/**
 * \brief Set SENS7 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SENS7_set_level(const bool level)
{
	PORTA_set_pin_level(6, level);
}

/**
 * \brief Toggle output level on SENS7
 *
 * Toggle the pin level
 */
static inline void SENS7_toggle_level()
{
	PORTA_toggle_pin_level(6);
}

/**
 * \brief Get level on SENS7
 *
 * Reads the level on a pin
 */
static inline bool SENS7_get_level()
{
	return PORTA_get_pin_level(6);
}

/**
 * \brief Set SENS8 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SENS8_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTA_set_pin_pull_mode(7, pull_mode);
}

/**
 * \brief Set SENS8 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SENS8_set_dir(const enum port_dir dir)
{
	PORTA_set_pin_dir(7, dir);
}

/**
 * \brief Set SENS8 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SENS8_set_level(const bool level)
{
	PORTA_set_pin_level(7, level);
}

/**
 * \brief Toggle output level on SENS8
 *
 * Toggle the pin level
 */
static inline void SENS8_toggle_level()
{
	PORTA_toggle_pin_level(7);
}

/**
 * \brief Get level on SENS8
 *
 * Reads the level on a pin
 */
static inline bool SENS8_get_level()
{
	return PORTA_get_pin_level(7);
}

/**
 * \brief Set SS pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SS_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTB_set_pin_pull_mode(0, pull_mode);
}

/**
 * \brief Set SS data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SS_set_dir(const enum port_dir dir)
{
	PORTB_set_pin_dir(0, dir);
}

/**
 * \brief Set SS level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SS_set_level(const bool level)
{
	PORTB_set_pin_level(0, level);
}

/**
 * \brief Toggle output level on SS
 *
 * Toggle the pin level
 */
static inline void SS_toggle_level()
{
	PORTB_toggle_pin_level(0);
}

/**
 * \brief Get level on SS
 *
 * Reads the level on a pin
 */
static inline bool SS_get_level()
{
	return PORTB_get_pin_level(0);
}

/**
 * \brief Set SCK pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SCK_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTB_set_pin_pull_mode(1, pull_mode);
}

/**
 * \brief Set SCK data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SCK_set_dir(const enum port_dir dir)
{
	PORTB_set_pin_dir(1, dir);
}

/**
 * \brief Set SCK level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SCK_set_level(const bool level)
{
	PORTB_set_pin_level(1, level);
}

/**
 * \brief Toggle output level on SCK
 *
 * Toggle the pin level
 */
static inline void SCK_toggle_level()
{
	PORTB_toggle_pin_level(1);
}

/**
 * \brief Get level on SCK
 *
 * Reads the level on a pin
 */
static inline bool SCK_get_level()
{
	return PORTB_get_pin_level(1);
}

/**
 * \brief Set MOSI pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void MOSI_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTB_set_pin_pull_mode(2, pull_mode);
}

/**
 * \brief Set MOSI data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void MOSI_set_dir(const enum port_dir dir)
{
	PORTB_set_pin_dir(2, dir);
}

/**
 * \brief Set MOSI level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void MOSI_set_level(const bool level)
{
	PORTB_set_pin_level(2, level);
}

/**
 * \brief Toggle output level on MOSI
 *
 * Toggle the pin level
 */
static inline void MOSI_toggle_level()
{
	PORTB_toggle_pin_level(2);
}

/**
 * \brief Get level on MOSI
 *
 * Reads the level on a pin
 */
static inline bool MOSI_get_level()
{
	return PORTB_get_pin_level(2);
}

/**
 * \brief Set MISO pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void MISO_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTB_set_pin_pull_mode(3, pull_mode);
}

/**
 * \brief Set MISO data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void MISO_set_dir(const enum port_dir dir)
{
	PORTB_set_pin_dir(3, dir);
}

/**
 * \brief Set MISO level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void MISO_set_level(const bool level)
{
	PORTB_set_pin_level(3, level);
}

/**
 * \brief Toggle output level on MISO
 *
 * Toggle the pin level
 */
static inline void MISO_toggle_level()
{
	PORTB_toggle_pin_level(3);
}

/**
 * \brief Get level on MISO
 *
 * Reads the level on a pin
 */
static inline bool MISO_get_level()
{
	return PORTB_get_pin_level(3);
}

/**
 * \brief Set LT pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void LT_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTB_set_pin_pull_mode(4, pull_mode);
}

/**
 * \brief Set LT data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void LT_set_dir(const enum port_dir dir)
{
	PORTB_set_pin_dir(4, dir);
}

/**
 * \brief Set LT level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void LT_set_level(const bool level)
{
	PORTB_set_pin_level(4, level);
}

/**
 * \brief Toggle output level on LT
 *
 * Toggle the pin level
 */
static inline void LT_toggle_level()
{
	PORTB_toggle_pin_level(4);
}

/**
 * \brief Get level on LT
 *
 * Reads the level on a pin
 */
static inline bool LT_get_level()
{
	return PORTB_get_pin_level(4);
}

/**
 * \brief Set PWM1 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void PWM1_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTB_set_pin_pull_mode(5, pull_mode);
}

/**
 * \brief Set PWM1 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void PWM1_set_dir(const enum port_dir dir)
{
	PORTB_set_pin_dir(5, dir);
}

/**
 * \brief Set PWM1 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void PWM1_set_level(const bool level)
{
	PORTB_set_pin_level(5, level);
}

/**
 * \brief Toggle output level on PWM1
 *
 * Toggle the pin level
 */
static inline void PWM1_toggle_level()
{
	PORTB_toggle_pin_level(5);
}

/**
 * \brief Get level on PWM1
 *
 * Reads the level on a pin
 */
static inline bool PWM1_get_level()
{
	return PORTB_get_pin_level(5);
}

/**
 * \brief Set PWM2 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void PWM2_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTB_set_pin_pull_mode(6, pull_mode);
}

/**
 * \brief Set PWM2 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void PWM2_set_dir(const enum port_dir dir)
{
	PORTB_set_pin_dir(6, dir);
}

/**
 * \brief Set PWM2 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void PWM2_set_level(const bool level)
{
	PORTB_set_pin_level(6, level);
}

/**
 * \brief Toggle output level on PWM2
 *
 * Toggle the pin level
 */
static inline void PWM2_toggle_level()
{
	PORTB_toggle_pin_level(6);
}

/**
 * \brief Get level on PWM2
 *
 * Reads the level on a pin
 */
static inline bool PWM2_get_level()
{
	return PORTB_get_pin_level(6);
}

/**
 * \brief Set PWM3 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void PWM3_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTB_set_pin_pull_mode(7, pull_mode);
}

/**
 * \brief Set PWM3 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void PWM3_set_dir(const enum port_dir dir)
{
	PORTB_set_pin_dir(7, dir);
}

/**
 * \brief Set PWM3 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void PWM3_set_level(const bool level)
{
	PORTB_set_pin_level(7, level);
}

/**
 * \brief Toggle output level on PWM3
 *
 * Toggle the pin level
 */
static inline void PWM3_toggle_level()
{
	PORTB_toggle_pin_level(7);
}

/**
 * \brief Get level on PWM3
 *
 * Reads the level on a pin
 */
static inline bool PWM3_get_level()
{
	return PORTB_get_pin_level(7);
}

/**
 * \brief Set DRV11 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void DRV11_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTC_set_pin_pull_mode(0, pull_mode);
}

/**
 * \brief Set DRV11 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void DRV11_set_dir(const enum port_dir dir)
{
	PORTC_set_pin_dir(0, dir);
}

/**
 * \brief Set DRV11 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void DRV11_set_level(const bool level)
{
	PORTC_set_pin_level(0, level);
}

/**
 * \brief Toggle output level on DRV11
 *
 * Toggle the pin level
 */
static inline void DRV11_toggle_level()
{
	PORTC_toggle_pin_level(0);
}

/**
 * \brief Get level on DRV11
 *
 * Reads the level on a pin
 */
static inline bool DRV11_get_level()
{
	return PORTC_get_pin_level(0);
}

/**
 * \brief Set DRV12 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void DRV12_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTC_set_pin_pull_mode(1, pull_mode);
}

/**
 * \brief Set DRV12 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void DRV12_set_dir(const enum port_dir dir)
{
	PORTC_set_pin_dir(1, dir);
}

/**
 * \brief Set DRV12 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void DRV12_set_level(const bool level)
{
	PORTC_set_pin_level(1, level);
}

/**
 * \brief Toggle output level on DRV12
 *
 * Toggle the pin level
 */
static inline void DRV12_toggle_level()
{
	PORTC_toggle_pin_level(1);
}

/**
 * \brief Get level on DRV12
 *
 * Reads the level on a pin
 */
static inline bool DRV12_get_level()
{
	return PORTC_get_pin_level(1);
}

/**
 * \brief Set DRV13 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void DRV13_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTC_set_pin_pull_mode(2, pull_mode);
}

/**
 * \brief Set DRV13 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void DRV13_set_dir(const enum port_dir dir)
{
	PORTC_set_pin_dir(2, dir);
}

/**
 * \brief Set DRV13 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void DRV13_set_level(const bool level)
{
	PORTC_set_pin_level(2, level);
}

/**
 * \brief Toggle output level on DRV13
 *
 * Toggle the pin level
 */
static inline void DRV13_toggle_level()
{
	PORTC_toggle_pin_level(2);
}

/**
 * \brief Get level on DRV13
 *
 * Reads the level on a pin
 */
static inline bool DRV13_get_level()
{
	return PORTC_get_pin_level(2);
}

/**
 * \brief Set DRV31 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void DRV31_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTC_set_pin_pull_mode(3, pull_mode);
}

/**
 * \brief Set DRV31 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void DRV31_set_dir(const enum port_dir dir)
{
	PORTC_set_pin_dir(3, dir);
}

/**
 * \brief Set DRV31 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void DRV31_set_level(const bool level)
{
	PORTC_set_pin_level(3, level);
}

/**
 * \brief Toggle output level on DRV31
 *
 * Toggle the pin level
 */
static inline void DRV31_toggle_level()
{
	PORTC_toggle_pin_level(3);
}

/**
 * \brief Get level on DRV31
 *
 * Reads the level on a pin
 */
static inline bool DRV31_get_level()
{
	return PORTC_get_pin_level(3);
}

/**
 * \brief Set DRV32 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void DRV32_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTC_set_pin_pull_mode(4, pull_mode);
}

/**
 * \brief Set DRV32 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void DRV32_set_dir(const enum port_dir dir)
{
	PORTC_set_pin_dir(4, dir);
}

/**
 * \brief Set DRV32 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void DRV32_set_level(const bool level)
{
	PORTC_set_pin_level(4, level);
}

/**
 * \brief Toggle output level on DRV32
 *
 * Toggle the pin level
 */
static inline void DRV32_toggle_level()
{
	PORTC_toggle_pin_level(4);
}

/**
 * \brief Get level on DRV32
 *
 * Reads the level on a pin
 */
static inline bool DRV32_get_level()
{
	return PORTC_get_pin_level(4);
}

/**
 * \brief Set DRV33 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void DRV33_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTC_set_pin_pull_mode(5, pull_mode);
}

/**
 * \brief Set DRV33 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void DRV33_set_dir(const enum port_dir dir)
{
	PORTC_set_pin_dir(5, dir);
}

/**
 * \brief Set DRV33 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void DRV33_set_level(const bool level)
{
	PORTC_set_pin_level(5, level);
}

/**
 * \brief Toggle output level on DRV33
 *
 * Toggle the pin level
 */
static inline void DRV33_toggle_level()
{
	PORTC_toggle_pin_level(5);
}

/**
 * \brief Get level on DRV33
 *
 * Reads the level on a pin
 */
static inline bool DRV33_get_level()
{
	return PORTC_get_pin_level(5);
}

/**
 * \brief Set DRV34 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void DRV34_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTC_set_pin_pull_mode(6, pull_mode);
}

/**
 * \brief Set DRV34 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void DRV34_set_dir(const enum port_dir dir)
{
	PORTC_set_pin_dir(6, dir);
}

/**
 * \brief Set DRV34 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void DRV34_set_level(const bool level)
{
	PORTC_set_pin_level(6, level);
}

/**
 * \brief Toggle output level on DRV34
 *
 * Toggle the pin level
 */
static inline void DRV34_toggle_level()
{
	PORTC_toggle_pin_level(6);
}

/**
 * \brief Get level on DRV34
 *
 * Reads the level on a pin
 */
static inline bool DRV34_get_level()
{
	return PORTC_get_pin_level(6);
}

/**
 * \brief Set DRV4 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void DRV4_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTC_set_pin_pull_mode(7, pull_mode);
}

/**
 * \brief Set DRV4 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void DRV4_set_dir(const enum port_dir dir)
{
	PORTC_set_pin_dir(7, dir);
}

/**
 * \brief Set DRV4 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void DRV4_set_level(const bool level)
{
	PORTC_set_pin_level(7, level);
}

/**
 * \brief Toggle output level on DRV4
 *
 * Toggle the pin level
 */
static inline void DRV4_toggle_level()
{
	PORTC_toggle_pin_level(7);
}

/**
 * \brief Get level on DRV4
 *
 * Reads the level on a pin
 */
static inline bool DRV4_get_level()
{
	return PORTC_get_pin_level(7);
}

/**
 * \brief Set HALL1 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void HALL1_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTD_set_pin_pull_mode(0, pull_mode);
}

/**
 * \brief Set HALL1 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void HALL1_set_dir(const enum port_dir dir)
{
	PORTD_set_pin_dir(0, dir);
}

/**
 * \brief Set HALL1 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void HALL1_set_level(const bool level)
{
	PORTD_set_pin_level(0, level);
}

/**
 * \brief Toggle output level on HALL1
 *
 * Toggle the pin level
 */
static inline void HALL1_toggle_level()
{
	PORTD_toggle_pin_level(0);
}

/**
 * \brief Get level on HALL1
 *
 * Reads the level on a pin
 */
static inline bool HALL1_get_level()
{
	return PORTD_get_pin_level(0);
}

/**
 * \brief Set HALL2 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void HALL2_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTD_set_pin_pull_mode(1, pull_mode);
}

/**
 * \brief Set HALL2 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void HALL2_set_dir(const enum port_dir dir)
{
	PORTD_set_pin_dir(1, dir);
}

/**
 * \brief Set HALL2 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void HALL2_set_level(const bool level)
{
	PORTD_set_pin_level(1, level);
}

/**
 * \brief Toggle output level on HALL2
 *
 * Toggle the pin level
 */
static inline void HALL2_toggle_level()
{
	PORTD_toggle_pin_level(1);
}

/**
 * \brief Get level on HALL2
 *
 * Reads the level on a pin
 */
static inline bool HALL2_get_level()
{
	return PORTD_get_pin_level(1);
}

/**
 * \brief Set HALL3 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void HALL3_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTD_set_pin_pull_mode(2, pull_mode);
}

/**
 * \brief Set HALL3 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void HALL3_set_dir(const enum port_dir dir)
{
	PORTD_set_pin_dir(2, dir);
}

/**
 * \brief Set HALL3 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void HALL3_set_level(const bool level)
{
	PORTD_set_pin_level(2, level);
}

/**
 * \brief Toggle output level on HALL3
 *
 * Toggle the pin level
 */
static inline void HALL3_toggle_level()
{
	PORTD_toggle_pin_level(2);
}

/**
 * \brief Get level on HALL3
 *
 * Reads the level on a pin
 */
static inline bool HALL3_get_level()
{
	return PORTD_get_pin_level(2);
}

/**
 * \brief Set HALL4 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void HALL4_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTD_set_pin_pull_mode(3, pull_mode);
}

/**
 * \brief Set HALL4 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void HALL4_set_dir(const enum port_dir dir)
{
	PORTD_set_pin_dir(3, dir);
}

/**
 * \brief Set HALL4 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void HALL4_set_level(const bool level)
{
	PORTD_set_pin_level(3, level);
}

/**
 * \brief Toggle output level on HALL4
 *
 * Toggle the pin level
 */
static inline void HALL4_toggle_level()
{
	PORTD_toggle_pin_level(3);
}

/**
 * \brief Get level on HALL4
 *
 * Reads the level on a pin
 */
static inline bool HALL4_get_level()
{
	return PORTD_get_pin_level(3);
}

/**
 * \brief Set LW_INH pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void LW_INH_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTD_set_pin_pull_mode(4, pull_mode);
}

/**
 * \brief Set LW_INH data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void LW_INH_set_dir(const enum port_dir dir)
{
	PORTD_set_pin_dir(4, dir);
}

/**
 * \brief Set LW_INH level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void LW_INH_set_level(const bool level)
{
	PORTD_set_pin_level(4, level);
}

/**
 * \brief Toggle output level on LW_INH
 *
 * Toggle the pin level
 */
static inline void LW_INH_toggle_level()
{
	PORTD_toggle_pin_level(4);
}

/**
 * \brief Get level on LW_INH
 *
 * Reads the level on a pin
 */
static inline bool LW_INH_get_level()
{
	return PORTD_get_pin_level(4);
}

/**
 * \brief Set RT pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void RT_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTD_set_pin_pull_mode(5, pull_mode);
}

/**
 * \brief Set RT data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void RT_set_dir(const enum port_dir dir)
{
	PORTD_set_pin_dir(5, dir);
}

/**
 * \brief Set RT level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void RT_set_level(const bool level)
{
	PORTD_set_pin_level(5, level);
}

/**
 * \brief Toggle output level on RT
 *
 * Toggle the pin level
 */
static inline void RT_toggle_level()
{
	PORTD_toggle_pin_level(5);
}

/**
 * \brief Get level on RT
 *
 * Reads the level on a pin
 */
static inline bool RT_get_level()
{
	return PORTD_get_pin_level(5);
}

/**
 * \brief Set RD pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void RD_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTD_set_pin_pull_mode(6, pull_mode);
}

/**
 * \brief Set RD data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void RD_set_dir(const enum port_dir dir)
{
	PORTD_set_pin_dir(6, dir);
}

/**
 * \brief Set RD level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void RD_set_level(const bool level)
{
	PORTD_set_pin_level(6, level);
}

/**
 * \brief Toggle output level on RD
 *
 * Toggle the pin level
 */
static inline void RD_toggle_level()
{
	PORTD_toggle_pin_level(6);
}

/**
 * \brief Get level on RD
 *
 * Reads the level on a pin
 */
static inline bool RD_get_level()
{
	return PORTD_get_pin_level(6);
}

/**
 * \brief Set RB pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void RB_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTD_set_pin_pull_mode(7, pull_mode);
}

/**
 * \brief Set RB data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void RB_set_dir(const enum port_dir dir)
{
	PORTD_set_pin_dir(7, dir);
}

/**
 * \brief Set RB level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void RB_set_level(const bool level)
{
	PORTD_set_pin_level(7, level);
}

/**
 * \brief Toggle output level on RB
 *
 * Toggle the pin level
 */
static inline void RB_toggle_level()
{
	PORTD_toggle_pin_level(7);
}

/**
 * \brief Get level on RB
 *
 * Reads the level on a pin
 */
static inline bool RB_get_level()
{
	return PORTD_get_pin_level(7);
}

/**
 * \brief Set RXD pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void RXD_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTE_set_pin_pull_mode(0, pull_mode);
}

/**
 * \brief Set RXD data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void RXD_set_dir(const enum port_dir dir)
{
	PORTE_set_pin_dir(0, dir);
}

/**
 * \brief Set RXD level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void RXD_set_level(const bool level)
{
	PORTE_set_pin_level(0, level);
}

/**
 * \brief Toggle output level on RXD
 *
 * Toggle the pin level
 */
static inline void RXD_toggle_level()
{
	PORTE_toggle_pin_level(0);
}

/**
 * \brief Get level on RXD
 *
 * Reads the level on a pin
 */
static inline bool RXD_get_level()
{
	return PORTE_get_pin_level(0);
}

/**
 * \brief Set TXD pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void TXD_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTE_set_pin_pull_mode(1, pull_mode);
}

/**
 * \brief Set TXD data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void TXD_set_dir(const enum port_dir dir)
{
	PORTE_set_pin_dir(1, dir);
}

/**
 * \brief Set TXD level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void TXD_set_level(const bool level)
{
	PORTE_set_pin_level(1, level);
}

/**
 * \brief Toggle output level on TXD
 *
 * Toggle the pin level
 */
static inline void TXD_toggle_level()
{
	PORTE_toggle_pin_level(1);
}

/**
 * \brief Get level on TXD
 *
 * Reads the level on a pin
 */
static inline bool TXD_get_level()
{
	return PORTE_get_pin_level(1);
}

/**
 * \brief Set CS pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void CS_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTE_set_pin_pull_mode(2, pull_mode);
}

/**
 * \brief Set CS data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void CS_set_dir(const enum port_dir dir)
{
	PORTE_set_pin_dir(2, dir);
}

/**
 * \brief Set CS level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void CS_set_level(const bool level)
{
	PORTE_set_pin_level(2, level);
}

/**
 * \brief Toggle output level on CS
 *
 * Toggle the pin level
 */
static inline void CS_toggle_level()
{
	PORTE_toggle_pin_level(2);
}

/**
 * \brief Get level on CS
 *
 * Reads the level on a pin
 */
static inline bool CS_get_level()
{
	return PORTE_get_pin_level(2);
}

/**
 * \brief Set PWM4 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void PWM4_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTE_set_pin_pull_mode(3, pull_mode);
}

/**
 * \brief Set PWM4 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void PWM4_set_dir(const enum port_dir dir)
{
	PORTE_set_pin_dir(3, dir);
}

/**
 * \brief Set PWM4 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void PWM4_set_level(const bool level)
{
	PORTE_set_pin_level(3, level);
}

/**
 * \brief Toggle output level on PWM4
 *
 * Toggle the pin level
 */
static inline void PWM4_toggle_level()
{
	PORTE_toggle_pin_level(3);
}

/**
 * \brief Get level on PWM4
 *
 * Reads the level on a pin
 */
static inline bool PWM4_get_level()
{
	return PORTE_get_pin_level(3);
}

/**
 * \brief Set PWM5 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void PWM5_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTE_set_pin_pull_mode(4, pull_mode);
}

/**
 * \brief Set PWM5 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void PWM5_set_dir(const enum port_dir dir)
{
	PORTE_set_pin_dir(4, dir);
}

/**
 * \brief Set PWM5 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void PWM5_set_level(const bool level)
{
	PORTE_set_pin_level(4, level);
}

/**
 * \brief Toggle output level on PWM5
 *
 * Toggle the pin level
 */
static inline void PWM5_toggle_level()
{
	PORTE_toggle_pin_level(4);
}

/**
 * \brief Get level on PWM5
 *
 * Reads the level on a pin
 */
static inline bool PWM5_get_level()
{
	return PORTE_get_pin_level(4);
}

/**
 * \brief Set PWM6 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void PWM6_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTE_set_pin_pull_mode(5, pull_mode);
}

/**
 * \brief Set PWM6 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void PWM6_set_dir(const enum port_dir dir)
{
	PORTE_set_pin_dir(5, dir);
}

/**
 * \brief Set PWM6 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void PWM6_set_level(const bool level)
{
	PORTE_set_pin_level(5, level);
}

/**
 * \brief Toggle output level on PWM6
 *
 * Toggle the pin level
 */
static inline void PWM6_toggle_level()
{
	PORTE_toggle_pin_level(5);
}

/**
 * \brief Get level on PWM6
 *
 * Reads the level on a pin
 */
static inline bool PWM6_get_level()
{
	return PORTE_get_pin_level(5);
}

/**
 * \brief Set DRV5 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void DRV5_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTE_set_pin_pull_mode(6, pull_mode);
}

/**
 * \brief Set DRV5 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void DRV5_set_dir(const enum port_dir dir)
{
	PORTE_set_pin_dir(6, dir);
}

/**
 * \brief Set DRV5 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void DRV5_set_level(const bool level)
{
	PORTE_set_pin_level(6, level);
}

/**
 * \brief Toggle output level on DRV5
 *
 * Toggle the pin level
 */
static inline void DRV5_toggle_level()
{
	PORTE_toggle_pin_level(6);
}

/**
 * \brief Get level on DRV5
 *
 * Reads the level on a pin
 */
static inline bool DRV5_get_level()
{
	return PORTE_get_pin_level(6);
}

/**
 * \brief Set DRV6 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void DRV6_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTE_set_pin_pull_mode(7, pull_mode);
}

/**
 * \brief Set DRV6 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void DRV6_set_dir(const enum port_dir dir)
{
	PORTE_set_pin_dir(7, dir);
}

/**
 * \brief Set DRV6 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void DRV6_set_level(const bool level)
{
	PORTE_set_pin_level(7, level);
}

/**
 * \brief Toggle output level on DRV6
 *
 * Toggle the pin level
 */
static inline void DRV6_toggle_level()
{
	PORTE_toggle_pin_level(7);
}

/**
 * \brief Get level on DRV6
 *
 * Reads the level on a pin
 */
static inline bool DRV6_get_level()
{
	return PORTE_get_pin_level(7);
}

/**
 * \brief Set ADC_CH1 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void ADC_CH1_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTF_set_pin_pull_mode(0, pull_mode);
}

/**
 * \brief Set ADC_CH1 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void ADC_CH1_set_dir(const enum port_dir dir)
{
	PORTF_set_pin_dir(0, dir);
}

/**
 * \brief Set ADC_CH1 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void ADC_CH1_set_level(const bool level)
{
	PORTF_set_pin_level(0, level);
}

/**
 * \brief Toggle output level on ADC_CH1
 *
 * Toggle the pin level
 */
static inline void ADC_CH1_toggle_level()
{
	PORTF_toggle_pin_level(0);
}

/**
 * \brief Get level on ADC_CH1
 *
 * Reads the level on a pin
 */
static inline bool ADC_CH1_get_level()
{
	return PORTF_get_pin_level(0);
}

/**
 * \brief Set ADC_CH2 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void ADC_CH2_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTF_set_pin_pull_mode(1, pull_mode);
}

/**
 * \brief Set ADC_CH2 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void ADC_CH2_set_dir(const enum port_dir dir)
{
	PORTF_set_pin_dir(1, dir);
}

/**
 * \brief Set ADC_CH2 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void ADC_CH2_set_level(const bool level)
{
	PORTF_set_pin_level(1, level);
}

/**
 * \brief Toggle output level on ADC_CH2
 *
 * Toggle the pin level
 */
static inline void ADC_CH2_toggle_level()
{
	PORTF_toggle_pin_level(1);
}

/**
 * \brief Get level on ADC_CH2
 *
 * Reads the level on a pin
 */
static inline bool ADC_CH2_get_level()
{
	return PORTF_get_pin_level(1);
}

/**
 * \brief Set SENS9 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SENS9_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTF_set_pin_pull_mode(2, pull_mode);
}

/**
 * \brief Set SENS9 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SENS9_set_dir(const enum port_dir dir)
{
	PORTF_set_pin_dir(2, dir);
}

/**
 * \brief Set SENS9 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SENS9_set_level(const bool level)
{
	PORTF_set_pin_level(2, level);
}

/**
 * \brief Toggle output level on SENS9
 *
 * Toggle the pin level
 */
static inline void SENS9_toggle_level()
{
	PORTF_toggle_pin_level(2);
}

/**
 * \brief Get level on SENS9
 *
 * Reads the level on a pin
 */
static inline bool SENS9_get_level()
{
	return PORTF_get_pin_level(2);
}

/**
 * \brief Set SENS10 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SENS10_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTF_set_pin_pull_mode(3, pull_mode);
}

/**
 * \brief Set SENS10 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SENS10_set_dir(const enum port_dir dir)
{
	PORTF_set_pin_dir(3, dir);
}

/**
 * \brief Set SENS10 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SENS10_set_level(const bool level)
{
	PORTF_set_pin_level(3, level);
}

/**
 * \brief Toggle output level on SENS10
 *
 * Toggle the pin level
 */
static inline void SENS10_toggle_level()
{
	PORTF_toggle_pin_level(3);
}

/**
 * \brief Get level on SENS10
 *
 * Reads the level on a pin
 */
static inline bool SENS10_get_level()
{
	return PORTF_get_pin_level(3);
}

/**
 * \brief Set SENS11 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void SENS11_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTF_set_pin_pull_mode(4, pull_mode);
}

/**
 * \brief Set SENS11 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void SENS11_set_dir(const enum port_dir dir)
{
	PORTF_set_pin_dir(4, dir);
}

/**
 * \brief Set SENS11 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void SENS11_set_level(const bool level)
{
	PORTF_set_pin_level(4, level);
}

/**
 * \brief Toggle output level on SENS11
 *
 * Toggle the pin level
 */
static inline void SENS11_toggle_level()
{
	PORTF_toggle_pin_level(4);
}

/**
 * \brief Get level on SENS11
 *
 * Reads the level on a pin
 */
static inline bool SENS11_get_level()
{
	return PORTF_get_pin_level(4);
}

/**
 * \brief Set ADC_POW pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void ADC_POW_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTF_set_pin_pull_mode(5, pull_mode);
}

/**
 * \brief Set ADC_POW data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void ADC_POW_set_dir(const enum port_dir dir)
{
	PORTF_set_pin_dir(5, dir);
}

/**
 * \brief Set ADC_POW level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void ADC_POW_set_level(const bool level)
{
	PORTF_set_pin_level(5, level);
}

/**
 * \brief Toggle output level on ADC_POW
 *
 * Toggle the pin level
 */
static inline void ADC_POW_toggle_level()
{
	PORTF_toggle_pin_level(5);
}

/**
 * \brief Get level on ADC_POW
 *
 * Reads the level on a pin
 */
static inline bool ADC_POW_get_level()
{
	return PORTF_get_pin_level(5);
}

/**
 * \brief Set ADC6 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void ADC6_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTF_set_pin_pull_mode(6, pull_mode);
}

/**
 * \brief Set ADC6 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void ADC6_set_dir(const enum port_dir dir)
{
	PORTF_set_pin_dir(6, dir);
}

/**
 * \brief Set ADC6 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void ADC6_set_level(const bool level)
{
	PORTF_set_pin_level(6, level);
}

/**
 * \brief Toggle output level on ADC6
 *
 * Toggle the pin level
 */
static inline void ADC6_toggle_level()
{
	PORTF_toggle_pin_level(6);
}

/**
 * \brief Get level on ADC6
 *
 * Reads the level on a pin
 */
static inline bool ADC6_get_level()
{
	return PORTF_get_pin_level(6);
}

/**
 * \brief Set ADC7 pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void ADC7_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTF_set_pin_pull_mode(7, pull_mode);
}

/**
 * \brief Set ADC7 data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void ADC7_set_dir(const enum port_dir dir)
{
	PORTF_set_pin_dir(7, dir);
}

/**
 * \brief Set ADC7 level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void ADC7_set_level(const bool level)
{
	PORTF_set_pin_level(7, level);
}

/**
 * \brief Toggle output level on ADC7
 *
 * Toggle the pin level
 */
static inline void ADC7_toggle_level()
{
	PORTF_toggle_pin_level(7);
}

/**
 * \brief Get level on ADC7
 *
 * Reads the level on a pin
 */
static inline bool ADC7_get_level()
{
	return PORTF_get_pin_level(7);
}

/**
 * \brief Set LW_HIGH pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void LW_HIGH_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTG_set_pin_pull_mode(0, pull_mode);
}

/**
 * \brief Set LW_HIGH data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void LW_HIGH_set_dir(const enum port_dir dir)
{
	PORTG_set_pin_dir(0, dir);
}

/**
 * \brief Set LW_HIGH level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void LW_HIGH_set_level(const bool level)
{
	PORTG_set_pin_level(0, level);
}

/**
 * \brief Toggle output level on LW_HIGH
 *
 * Toggle the pin level
 */
static inline void LW_HIGH_toggle_level()
{
	PORTG_toggle_pin_level(0);
}

/**
 * \brief Get level on LW_HIGH
 *
 * Reads the level on a pin
 */
static inline bool LW_HIGH_get_level()
{
	return PORTG_get_pin_level(0);
}

/**
 * \brief Set LW_LOW pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void LW_LOW_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTG_set_pin_pull_mode(1, pull_mode);
}

/**
 * \brief Set LW_LOW data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void LW_LOW_set_dir(const enum port_dir dir)
{
	PORTG_set_pin_dir(1, dir);
}

/**
 * \brief Set LW_LOW level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void LW_LOW_set_level(const bool level)
{
	PORTG_set_pin_level(1, level);
}

/**
 * \brief Toggle output level on LW_LOW
 *
 * Toggle the pin level
 */
static inline void LW_LOW_toggle_level()
{
	PORTG_toggle_pin_level(1);
}

/**
 * \brief Get level on LW_LOW
 *
 * Reads the level on a pin
 */
static inline bool LW_LOW_get_level()
{
	return PORTG_get_pin_level(1);
}

/**
 * \brief Set RW_HIGH pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void RW_HIGH_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTG_set_pin_pull_mode(2, pull_mode);
}

/**
 * \brief Set RW_HIGH data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void RW_HIGH_set_dir(const enum port_dir dir)
{
	PORTG_set_pin_dir(2, dir);
}

/**
 * \brief Set RW_HIGH level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void RW_HIGH_set_level(const bool level)
{
	PORTG_set_pin_level(2, level);
}

/**
 * \brief Toggle output level on RW_HIGH
 *
 * Toggle the pin level
 */
static inline void RW_HIGH_toggle_level()
{
	PORTG_toggle_pin_level(2);
}

/**
 * \brief Get level on RW_HIGH
 *
 * Reads the level on a pin
 */
static inline bool RW_HIGH_get_level()
{
	return PORTG_get_pin_level(2);
}

/**
 * \brief Set RW_LOW pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void RW_LOW_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTG_set_pin_pull_mode(3, pull_mode);
}

/**
 * \brief Set RW_LOW data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void RW_LOW_set_dir(const enum port_dir dir)
{
	PORTG_set_pin_dir(3, dir);
}

/**
 * \brief Set RW_LOW level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void RW_LOW_set_level(const bool level)
{
	PORTG_set_pin_level(3, level);
}

/**
 * \brief Toggle output level on RW_LOW
 *
 * Toggle the pin level
 */
static inline void RW_LOW_toggle_level()
{
	PORTG_toggle_pin_level(3);
}

/**
 * \brief Get level on RW_LOW
 *
 * Reads the level on a pin
 */
static inline bool RW_LOW_get_level()
{
	return PORTG_get_pin_level(3);
}

/**
 * \brief Set RW_INH pull mode
 *
 * Configure pin to pull up, down or disable pull mode, supported pull
 * modes are defined by device used
 *
 * \param[in] pull_mode Pin pull mode
 */
static inline void RW_INH_set_pull_mode(const enum port_pull_mode pull_mode)
{
	PORTG_set_pin_pull_mode(4, pull_mode);
}

/**
 * \brief Set RW_INH data direction
 *
 * Select if the pin data direction is input, output or disabled.
 * If disabled state is not possible, this function throws an assert.
 *
 * \param[in] direction PORT_DIR_IN  = Data direction in
 *                      PORT_DIR_OUT = Data direction out
 *                      PORT_DIR_OFF = Disables the pin
 *                      (low power state)
 */
static inline void RW_INH_set_dir(const enum port_dir dir)
{
	PORTG_set_pin_dir(4, dir);
}

/**
 * \brief Set RW_INH level
 *
 * Sets output level on a pin
 *
 * \param[in] level true  = Pin level set to "high" state
 *                  false = Pin level set to "low" state
 */
static inline void RW_INH_set_level(const bool level)
{
	PORTG_set_pin_level(4, level);
}

/**
 * \brief Toggle output level on RW_INH
 *
 * Toggle the pin level
 */
static inline void RW_INH_toggle_level()
{
	PORTG_toggle_pin_level(4);
}

/**
 * \brief Get level on RW_INH
 *
 * Reads the level on a pin
 */
static inline bool RW_INH_get_level()
{
	return PORTG_get_pin_level(4);
}

#endif /* ATMEL_START_PINS_H_INCLUDED */
