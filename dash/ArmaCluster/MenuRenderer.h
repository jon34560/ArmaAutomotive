#pragma once

# include "ObjectRendererBase.h"

class MenuRenderer : public ObjectRendererBase
{
private:
	unsigned int bckgroundTexture;
	unsigned int carImageTexture;
	int selectedRectStartX;
	int selectedRectStartY;

	int selBoxIndex;
	int selBoxIndexLevel1;
	int selBoxIndexLevel2;
	int nSelBoxHorizLevelIndex;

	int Level1MaxLimit;
	int Level2MaxLimit;

public:
	MenuRenderer();
	~MenuRenderer();
	void initialize();
	void doRender();
	void keyPressed(int key);
	int getSelecedIndex();
	int getHorizLevelIndex();
	void drawCar();
	void setSubMenuMaxLimits(int level1MaxLimit, int level2MaxLimit);
	void reset();
};