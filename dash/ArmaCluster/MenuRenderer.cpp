#include "MenuRenderer.h"
#include "stb_image.h"
#include <string>

#define  KEY_UP                        0x0065
#define  KEY_DOWN                      0x0067
#define  KEY_LEFT                      0x0064
#define  KEY_RIGHT                     0x0066

MenuRenderer::MenuRenderer()
{
	selectedRectStartX = 50;
	selectedRectStartY = 537;

	selBoxIndex = 1;
	selBoxIndexLevel1 = 1;
	selBoxIndexLevel2 = 1;
	nSelBoxHorizLevelIndex = 0;
	Level1MaxLimit = 3;
	Level1MaxLimit = 3;
}

MenuRenderer::~MenuRenderer()
{

}

void MenuRenderer::initialize()
{
	int width, height, nrChannels;
    std::string image_path;
    #ifdef _WIN32
        image_path = ".\\images\\selected.png";
    #else
        image_path = "./images/selected.png";
    #endif
	unsigned char *data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &bckgroundTexture);

	glBindTexture(GL_TEXTURE_2D, bckgroundTexture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	else
	{
		// ToDO : Handle error
	}
	stbi_image_free(data);

    #ifdef _WIN32
        image_path = ".\\images\\car.png";
    #else
        image_path = "./images/car.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &carImageTexture);

	glBindTexture(GL_TEXTURE_2D, carImageTexture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	else
	{
		// ToDO : Handle error
	}
	stbi_image_free(data);
}

void MenuRenderer::doRender()
{
	//drawCar(); // temp disable until better images are ready.

	glPushMatrix();

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, bckgroundTexture);

	
	if(0== nSelBoxHorizLevelIndex)
		glViewport(selectedRectStartX - 11, selectedRectStartY-(selBoxIndex-1)*41, 168, 40);
	else if(1== nSelBoxHorizLevelIndex)
		glViewport(selectedRectStartX - 11, selectedRectStartY - (selBoxIndexLevel1 - 1) * 41, 168, 40);
	else if(2== nSelBoxHorizLevelIndex)
		glViewport(selectedRectStartX - 15, selectedRectStartY - (selBoxIndexLevel2 - 1) * 41, 168, 40);
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

	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

/**
* keyPressed
* Description: Called by TextOverlayRenderer
*/
void MenuRenderer::keyPressed(int key)
{
	if (key == KEY_RIGHT)
	{
		nSelBoxHorizLevelIndex++;
		if (nSelBoxHorizLevelIndex < 3)
			selectedRectStartX = 50 + (200)*nSelBoxHorizLevelIndex;
		else
			nSelBoxHorizLevelIndex--;

		if (nSelBoxHorizLevelIndex == 1)
		{
			selectedRectStartY = 537;
			selBoxIndexLevel1 = 1;
		}
		if (nSelBoxHorizLevelIndex == 2)
		{
			selectedRectStartY = 537;
			selBoxIndexLevel2 = 1;
		}
	}
	if (key == KEY_LEFT)
	{
		nSelBoxHorizLevelIndex--;
		if (nSelBoxHorizLevelIndex >= 0)
			selectedRectStartX = 50 + (200)*nSelBoxHorizLevelIndex;
		else
			nSelBoxHorizLevelIndex = 0;
		
		if (nSelBoxHorizLevelIndex == 1)
		{
			selectedRectStartY = 537;
			selBoxIndexLevel1 = 1;
		}
		if (nSelBoxHorizLevelIndex == 2)
		{
			selectedRectStartY = 537;
			selBoxIndexLevel2 = 1;
		}
	}
	if (nSelBoxHorizLevelIndex == 0)
	{
		if (selBoxIndex > 0 && selBoxIndex <= 3)
		{
			if (key == KEY_UP)
			{
				selBoxIndex--;
			}
			if (key == KEY_DOWN)
			{
				selBoxIndex++;
			}
		}
		if (selBoxIndex > 3)
			selBoxIndex = 3;
		if (selBoxIndex < 1)
			selBoxIndex = 1;
	}
	// Submenu Level1
	if (nSelBoxHorizLevelIndex == 1)
	{
		if (selBoxIndexLevel1 > 0 && selBoxIndexLevel1 <= 3)
		{
			if (key == KEY_UP)
			{
				selBoxIndexLevel1--;
			}
			if (key == KEY_DOWN)
			{
				selBoxIndexLevel1++;
			}
		}
		if (selBoxIndexLevel1 > Level1MaxLimit)
			selBoxIndexLevel1 = Level1MaxLimit;
		if (selBoxIndexLevel1 < 1)
			selBoxIndexLevel1 = 1;
	}
	// Submenu Level2
	if (nSelBoxHorizLevelIndex == 2)
	{
		if (selBoxIndexLevel2 > 0 && selBoxIndexLevel2 <= 3)
		{
			if (key == KEY_UP)
			{
				selBoxIndexLevel2--;
			}
			if (key == KEY_DOWN)
			{
				selBoxIndexLevel2++;
			}
		}
		if (selBoxIndexLevel2 > Level2MaxLimit)
			selBoxIndexLevel2 = Level2MaxLimit;
		if (selBoxIndexLevel2 < 1)
			selBoxIndexLevel2 = 1;
	}
}

int MenuRenderer::getSelecedIndex()
{
	return selBoxIndex;
}

int MenuRenderer::getHorizLevelIndex()
{
	return nSelBoxHorizLevelIndex;
}

void MenuRenderer::drawCar()
{
	glPushMatrix();

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, carImageTexture);
	glViewport(242,44,166,428);
	glRotatef(180, 1, 0, 0);

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

	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void MenuRenderer::setSubMenuMaxLimits(int level1MaxLimit, int level2MaxLimit)
{
	Level1MaxLimit = level1MaxLimit;
	Level2MaxLimit = level2MaxLimit;
}

void MenuRenderer::reset()
{
	selectedRectStartX = 50;
	selectedRectStartY = 537;

	selBoxIndex = 1;
	selBoxIndexLevel1 = 1;
	selBoxIndexLevel2 = 1;
	nSelBoxHorizLevelIndex = 0;
	Level1MaxLimit = 3;
	Level1MaxLimit = 3;
}
