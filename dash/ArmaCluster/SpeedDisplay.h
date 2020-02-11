#pragma once
# include "ObjectRendererBase.h"

class SpeedDisplay : public ObjectRendererBase
{
private:
	unsigned int digit0Texture;
	unsigned int digit1Texture;
	unsigned int digit2Texture;
	unsigned int digit3Texture;
	unsigned int digit4Texture;
	unsigned int digit5Texture;
	unsigned int digit6Texture;
	unsigned int digit7Texture;
	unsigned int digit8Texture;
	unsigned int digit9Texture;
public:
	SpeedDisplay();
	~SpeedDisplay();
	void initialize();
	void doRender();
	void drawDigit(int ndigit,int nDigitOrder, int nOffset);
	void keyPressed(int key);
};