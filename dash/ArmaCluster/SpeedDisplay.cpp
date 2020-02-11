#include "SpeedDisplay.h"
#include "stb_image.h"

int testAngle = 0;
SpeedDisplay::SpeedDisplay()
{
	digit0Texture = -1;
	digit1Texture = -1;
	digit2Texture = -1;
	digit3Texture = -1;
	digit4Texture = -1;
	digit5Texture = -1;
	digit6Texture = -1;
	digit7Texture = -1;
	digit8Texture = -1;
	digit9Texture = -1;
}

SpeedDisplay::~SpeedDisplay()
{

}

void SpeedDisplay::initialize()
{
	int width, height, nrChannels;
	unsigned char *data = stbi_load(".\\images\\0.png", &width, &height, &nrChannels, 0);
	glGenTextures(1, &digit0Texture);
	glBindTexture(GL_TEXTURE_2D, digit0Texture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Digit 1
	data = stbi_load(".\\images\\1.png", &width, &height, &nrChannels, 0);
	glGenTextures(1, &digit1Texture);
	glBindTexture(GL_TEXTURE_2D, digit1Texture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Digit 2
	data = stbi_load(".\\images\\2.png", &width, &height, &nrChannels, 0);
	glGenTextures(1, &digit2Texture);
	glBindTexture(GL_TEXTURE_2D, digit2Texture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Digit 3
	data = stbi_load(".\\images\\3.png", &width, &height, &nrChannels, 0);
	glGenTextures(1, &digit3Texture);
	glBindTexture(GL_TEXTURE_2D, digit3Texture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Digit 4
	data = stbi_load(".\\images\\4.png", &width, &height, &nrChannels, 0);
	glGenTextures(1, &digit4Texture);
	glBindTexture(GL_TEXTURE_2D, digit4Texture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Digit 5
	data = stbi_load(".\\images\\5.png", &width, &height, &nrChannels, 0);
	glGenTextures(1, &digit5Texture);
	glBindTexture(GL_TEXTURE_2D, digit5Texture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Digit 6
	data = stbi_load(".\\images\\6.png", &width, &height, &nrChannels, 0);
	glGenTextures(1, &digit6Texture);
	glBindTexture(GL_TEXTURE_2D, digit6Texture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Digit 7
	data = stbi_load(".\\images\\7.png", &width, &height, &nrChannels, 0);
	glGenTextures(1, &digit7Texture);
	glBindTexture(GL_TEXTURE_2D, digit7Texture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Digit 8
	data = stbi_load(".\\images\\8.png", &width, &height, &nrChannels, 0);
	glGenTextures(1, &digit8Texture);
	glBindTexture(GL_TEXTURE_2D, digit8Texture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Digit 9
	data = stbi_load(".\\images\\9.png", &width, &height, &nrChannels, 0);
	glGenTextures(1, &digit9Texture);
	glBindTexture(GL_TEXTURE_2D, digit9Texture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);
}

void SpeedDisplay::doRender()
{
	int nCurSpeed = testAngle;// 124;
	int digit1 = 0;
	int digit2 = 0;
	int digit3 = 0;
	int nNoOfDigits = 0;
	int nOffset = 0;

	if (nCurSpeed / 100 != 0)
	{
		nNoOfDigits = 3;
		digit1 = nCurSpeed % 10;
		nCurSpeed = nCurSpeed / 10;
		digit2 = nCurSpeed % 10;
		nCurSpeed = nCurSpeed / 10;
		digit3 = nCurSpeed % 10;
	}
	else if (nCurSpeed / 10 != 0)
	{
		nNoOfDigits = 2;
		nOffset = 40;
		digit2 = nCurSpeed % 10;
		nCurSpeed = nCurSpeed / 10;
		digit3 = nCurSpeed % 10;
	}
	else
	{
		nOffset = 70;
		nNoOfDigits = 1;
		digit3 = nCurSpeed % 10;
	}

	if (3 == nNoOfDigits)
	{
		drawDigit(digit1, 3, 0);
		drawDigit(digit2, 2, 0);
		drawDigit(digit3, 1, 0);
	}
	else if (2 == nNoOfDigits)
	{
		drawDigit(digit2, 2, nOffset);
		drawDigit(digit3, 1, nOffset);
	}
	else
		drawDigit(digit3, 1, 100);

	testAngle++;
	if (testAngle > 360)
		testAngle = 0;

}


/**
* drawDigit
*
* Description: 
* @param: ndigit - [0|9]
* @param: nDigitOrder - [1|2|3]
* @param: nOffset - 
*/
void SpeedDisplay::drawDigit(int ndigit, int nDigitOrder, int nOffset)
{
	glPushMatrix();

	glEnable(GL_TEXTURE_2D);
	if(0 == ndigit)
		glBindTexture(GL_TEXTURE_2D, digit0Texture);
	else if (1 == ndigit)
		glBindTexture(GL_TEXTURE_2D, digit1Texture);
	else if (2 == ndigit)
		glBindTexture(GL_TEXTURE_2D, digit2Texture);
	else if (3 == ndigit)
		glBindTexture(GL_TEXTURE_2D, digit3Texture);
	else if (4 == ndigit)
		glBindTexture(GL_TEXTURE_2D, digit4Texture);
	else if (5 == ndigit)
		glBindTexture(GL_TEXTURE_2D, digit5Texture);
	else if (6 == ndigit)
		glBindTexture(GL_TEXTURE_2D, digit6Texture);
	else if (7 == ndigit)
		glBindTexture(GL_TEXTURE_2D, digit7Texture);
	else if (8 == ndigit)
		glBindTexture(GL_TEXTURE_2D, digit8Texture);
	else if (9 == ndigit)
		glBindTexture(GL_TEXTURE_2D, digit9Texture);

	if( 1 == nDigitOrder)
		glViewport(847+ nOffset, 386 - 52, 69, 94); // x, y, w, h
	else if (2 == nDigitOrder)
		glViewport(923+ nOffset, 386 - 52, 69, 94);
	else if (3 == nDigitOrder)
		glViewport(999+ nOffset, 386 - 52, 69, 94);

	glRotatef(180.0, 1, 0, 0);
	glBegin(GL_QUADS);

	glTexCoord2f(0.0, 0.0);
	glVertex2f(-1.0, -1.0);

	glTexCoord2f(0.0, 1.0);
	glVertex2f(-1.0, 1.0);

	glTexCoord2f(1.0, 1.0);
	glVertex2f(1.0, 1.0);

	glTexCoord2f(1.0, 0.0);
	glVertex2f(1.0, -1.0);

	glEnd();
	//glFlush();
	glDisable(GL_TEXTURE_2D);

	glPopMatrix();
}

void SpeedDisplay::keyPressed(int key)
{

}
