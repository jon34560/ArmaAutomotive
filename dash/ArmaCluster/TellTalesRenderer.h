#pragma once

# include "ObjectRendererBase.h"

class TellTailesRenderer : public ObjectRendererBase
{
private:
	unsigned int lightOnTexture;
	unsigned int lightOffTexture;

	unsigned int hazardOnTexture;
	unsigned int hazardOffTexture;

	unsigned int parkOnTexture;
	unsigned int parkOffTexture;

	unsigned int rightOnTexture;
	unsigned int rightOffTexture;

	unsigned int leftOnTexture;
	unsigned int leftOffTexture;

	unsigned int checkOnTexture;
	unsigned int checkOffTexture;

	unsigned int seatbeltOnTexture;
	unsigned int seatbeltOffTexture;

	unsigned int doorAjarOnTexture;
	unsigned int doorAjarOffTexture;
public:
	TellTailesRenderer();
	~TellTailesRenderer();
	void initialize();
	void doRender();
	void drawTellTale();
	void keyPressed(int key);
};