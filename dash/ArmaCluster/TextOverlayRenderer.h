#pragma once

# include "ObjectRendererBase.h" // ???
#include "MenuRenderer.h"

class TextOverlayRenderer : public ObjectRendererBase
{
private:
	MenuRenderer menuRendererObj;
public:
	TextOverlayRenderer();
	~TextOverlayRenderer();
	void initialize();
	void doRender();
	void drawMainMenu();
	void keyPressed(int key);
	void ChangeMainMenuWithIndex(int index);
};