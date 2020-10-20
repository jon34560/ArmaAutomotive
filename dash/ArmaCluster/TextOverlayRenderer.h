#pragma once

# include "ObjectRendererBase.h" // ???
#include "MenuRenderer.h"
#include "ClockRenderer.h"

class TextOverlayRenderer : public ObjectRendererBase
{
private:
	MenuRenderer menuRendererObj;
	ClockRenderer clockRendererObj;
	int displayMode = 0;
public:
	TextOverlayRenderer();
	~TextOverlayRenderer();
	void initialize();
	void doRender();
	void drawMainMenu();
	void keyPressed(int key);
	void ChangeMainMenuWithIndex(int index);
	void setDisplayMode(int mode);
	void DrawLeftBox();
	void DrawRectangle(float x, float y, float width, float height);
};