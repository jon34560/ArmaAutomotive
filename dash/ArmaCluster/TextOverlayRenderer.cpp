/**
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
*/

#include "TextOverlayRenderer.h"

#ifndef TEX_REND
#include "bitmap_fonts.h"
#endif
#include "DataModel.h"
#ifndef TEX_REND
#define TEX_REND
#endif

#include "stb_image.h"
#include "DataModel.h"
#include "stb_image.h"
#include <string>
#include <vector>

#include <iostream>
#include <chrono>
#include <ctime>  

using namespace std;
#define  KEY_UP                        0x0065
#define  KEY_DOWN                      0x0067
#define  KEY_LEFT                      0x0064
#define  KEY_RIGHT                     0x0066
#define  MAIN_MENU_CNT                 7

vector<string> mainMenuItems;
vector<string> subMenu1_2;
vector<string> subMenu1_3;
vector<string> subMenu1_4;

vector<vector<string>> *ptrSubMenuLevel1;

vector<vector<string>> subMenu1Level1;
vector<vector<string>> subMenu2Level1;
vector<vector<string>> subMenu3Level1;
vector<vector<string>> subMenu4Level1;
vector<vector<string>> subMenu5Level1;
vector<vector<string>> subMenu6Level1;
vector<vector<string>> subMenu7Level1;

int nMainMenuStartIndex = 0;
int activeMenuSelection[3]; // 0 - Main menu, 1 - Level1 menu, 2 - Level2 menu

TextOverlayRenderer::TextOverlayRenderer()
{
	activeMenuSelection[0] = 0;
	activeMenuSelection[1] = 0;
	activeMenuSelection[2] = 0;
}

TextOverlayRenderer::~TextOverlayRenderer()
{

}

/*
* initalize
* Description: 
*/
void TextOverlayRenderer::initialize()
{
	menuRendererObj.initialize();
	mainMenuItems.push_back("Locks");
	mainMenuItems.push_back("Windows");
	mainMenuItems.push_back("Climate");
	mainMenuItems.push_back("Steering");
	mainMenuItems.push_back("Odometer");
	mainMenuItems.push_back("Clock");
	mainMenuItems.push_back("Units");

	vector<string> subMenu1_1;
	subMenu1_1.push_back("Doors");
	subMenu1_1.push_back("Unlock");
	subMenu1_1.push_back("Lock");
	subMenu1Level1.push_back(subMenu1_1);
	vector<string> subMenu1_2;
	subMenu1_2.push_back("Fuel");
	subMenu1_2.push_back("Open");
	subMenu1Level1.push_back(subMenu1_2);

	vector<string> subMenu2_1;
	subMenu2_1.push_back("Left");
	subMenu2_1.push_back("Up");
	subMenu2_1.push_back("Down");
	subMenu2Level1.push_back(subMenu2_1);
	vector<string> subMenu2_2;
	subMenu2_2.push_back("Right");
	subMenu2_2.push_back("Up");
	subMenu2_2.push_back("Down");
	subMenu2Level1.push_back(subMenu2_2);

	vector<string> subMenu3_1;
	subMenu3_1.push_back("Fans");
	subMenu3_1.push_back("Up");
	subMenu3_1.push_back("Down");
	subMenu3Level1.push_back(subMenu3_1);

	vector<string> subMenu4_1;
	subMenu4_1.push_back("Assist");
	subMenu4_1.push_back("Up");
	subMenu4_1.push_back("Down");
	subMenu4Level1.push_back(subMenu4_1);

	vector<string> subMenu5_1;
	subMenu5_1.push_back("Reset");
	subMenu5Level1.push_back(subMenu5_1);

	vector<string> subMenu6_1;
	subMenu6_1.push_back("Hour");
	subMenu6_1.push_back("Up");
	subMenu6_1.push_back("Down");
	subMenu6Level1.push_back(subMenu6_1);
	vector<string> subMenu6_2;
	subMenu6_2.push_back("Minute");
	subMenu6_2.push_back("Up");
	subMenu6_2.push_back("Down");
	subMenu6Level1.push_back(subMenu6_2);

	vector<string> subMenu7_1;
	subMenu7_1.push_back("Metric");
	subMenu7Level1.push_back(subMenu7_1);
	vector<string> subMenu7_2;
	subMenu7_2.push_back("Imperial");
	subMenu7Level1.push_back(subMenu7_2);

	ptrSubMenuLevel1 = &subMenu1Level1;
}

void TextOverlayRenderer::doRender()
{
	glViewport(0, 0, 1920, 720);
	beginRenderText(1920, 720);
	{
		glColor3f(1.0f, 1.0f, 1.0f);

		int fuelVal = DataModel::getInstance()->getfuelValue();
		const char char_fuelmax = -1;
		std::string f = std::to_string(fuelVal);
		f = "Fuel " + f + " L";
		char const* fuelValchar = f.c_str();

		int temperatureVal = DataModel::getInstance()->gettemperatureValue();
		const char char_temperaturemax = -1;
		std::string t = std::to_string(temperatureVal);
		t = "Temp " + t + " c";
		char const* temperatureValchar = t.c_str();

		int rangeVal = DataModel::getInstance()->getrangeValue();
		const char char_rangemax = -1;
		std::string r = std::to_string(rangeVal);
		r = "Range " + r + " km";
		char const* rangeValchar = r.c_str();

		int pressureVal = DataModel::getInstance()->getpressureValue();
		const char char_pressuremax = -1;
		std::string p = std::to_string(pressureVal);
		p = "Pressure " + p + " psi";
		char const* pressureValchar = p.c_str();

		int voltageVal = DataModel::getInstance()->getvoltageValue();
		const char char_voltagemax = -1;
		std::string v = std::to_string(voltageVal);
		v = v + " V";
		char const* voltageValchar = v.c_str();

		int odometerVal = DataModel::getInstance()->getodometerValue();
		const char char_odometermax = -1;
		std::string o = std::to_string(odometerVal);
		o = o + " Km";
		char const* odometerValchar = o.c_str();

		std::string timeVal = DataModel::getInstance()->gettimeValue();
		//auto time = std::chrono::system_clock::now();
		std::time_t tm = std::time(0);   // get time now
		std::tm* now = std::localtime(&tm);
		
		const char char_timemax = -1;
		std::string m = "AM";
		int hour = now->tm_hour;
		if (hour > 12 ) {
			hour = hour - 12;
			m = "PM";
		}
		std::string minute = std::to_string(now->tm_min);
		if (now->tm_min < 10) {
			minute = "0" + minute;
		}
		std::string second = std::to_string(now->tm_sec);
		if (now->tm_sec < 10) {
			second = "0" + second;
		}
		std::string xxx;
		xxx = std::to_string(hour) + " : " + minute + " : " + second + " " + m;

		char const* timeValchar = xxx.c_str();
		

		if (displayMode == 1) {	// dials
			renderText(1400 + 5, 223 + 23, BITMAP_FONT_TYPE_HELVETICA_18, fuelValchar); // Fuel  BITMAP_FONT_TYPE_TIMES_ROMAN_24
			renderText(1648 + 0, 223 + 23, BITMAP_FONT_TYPE_HELVETICA_18, rangeValchar); // Range
			renderText(1396, 467 + 39, BITMAP_FONT_TYPE_HELVETICA_18, temperatureValchar); // Temp
			renderText(1642, 467 + 39, BITMAP_FONT_TYPE_HELVETICA_18, pressureValchar); // Oil pressure
		}

		if (displayMode != 1) {	// bars
			renderText(1300 + 10, 283 - 8, BITMAP_FONT_TYPE_HELVETICA_18, fuelValchar); // Fuel  BITMAP_FONT_TYPE_TIMES_ROMAN_24
			renderText(1300 + 10, 283 + 50, BITMAP_FONT_TYPE_HELVETICA_18, rangeValchar); // Range
			renderText(1300 + 10, 283 + 108, BITMAP_FONT_TYPE_HELVETICA_18, temperatureValchar); // Temp
			renderText(1300 + 10, 283 + 166, BITMAP_FONT_TYPE_HELVETICA_18, pressureValchar); // Oil pressure
		}

		renderText(1557, 680, BITMAP_FONT_TYPE_HELVETICA_18, voltageValchar); // voltage
		renderText(1738, 684, BITMAP_FONT_TYPE_HELVETICA_18, odometerValchar); // odo
		renderText(1300, 684, BITMAP_FONT_TYPE_HELVETICA_18, timeValchar); // time

		// Speed units
		renderText(940, 433 + 6, BITMAP_FONT_TYPE_HELVETICA_18, "Kmh"); // Speed Units
		// Helvetica18 BITMAP_FONT_TYPE_TIMES_ROMAN_24


		glColor3f(1.0f, 1.0f, 1.0f);
	}
	endRenderText();

	if (DataModel::getInstance()->isMenuDisplayed())
	{
		menuRendererObj.setSubMenuMaxLimits(ptrSubMenuLevel1->size(), ptrSubMenuLevel1->at(activeMenuSelection[1]).size() - 1);
		drawMainMenu();
		menuRendererObj.doRender();

		DrawLeftBox();
	}
	else
	{
		// Just print settings text
		/*
		beginRenderText(1920, 720);
		{
			glColor3f(1.0f, 1.0f, 1.0f);
			renderText(80 - 27, 170, BITMAP_FONT_TYPE_HELVETICA_18, "Settings");
			glColor3f(1.0f, 1.0f, 1.0f);
		}
		endRenderText();
		*/

		// Draw clock 
		//clockRendererObj.doRender();
		clockRendererObj.drawClock();


		activeMenuSelection[0] = 0;
		activeMenuSelection[1] = 0;
		activeMenuSelection[2] = 0;
		ptrSubMenuLevel1 = &subMenu1Level1;
		menuRendererObj.reset();
	}
}

void TextOverlayRenderer::drawMainMenu()
{
	glViewport(0, 0, 1920, 720);

	// Render main menu
	int yOffset = 41;				// distance between rows
	int nNoOfMenu = mainMenuItems.size();
	for (int nIndex = 0; nIndex < 3; nIndex++)
	{
		int menuStartIndex = nMainMenuStartIndex+ nIndex;
		if (menuStartIndex >= (nNoOfMenu))
			break;
		beginRenderText(1920, 720);
		{
			glColor3f(1.0f, 1.0f, 1.0f);
			renderText((float)80 - 20 - 3, (float)170 + (yOffset * nIndex), BITMAP_FONT_TYPE_HELVETICA_18, mainMenuItems.at(menuStartIndex).c_str());
			glColor3f(1.0f, 1.0f, 1.0f);
		}
		endRenderText();
	}

	// Render level1 submenu
	int nNoOfSubMenuLevel1Items = ptrSubMenuLevel1->size();
	for (int nIndex = 0; nIndex < 3; nIndex++)
	{
		if (nIndex > (nNoOfSubMenuLevel1Items - 1))
			break;
		beginRenderText(1920, 720);
		{
			string temp = ptrSubMenuLevel1->at(nIndex).at(0);
			glColor3f(1.0f, 1.0f, 1.0f);
			renderText((float)290 - 20 - 10 - 3, (float)170 + (yOffset * nIndex), BITMAP_FONT_TYPE_HELVETICA_18, temp.c_str());
			glColor3f(1.0f, 1.0f, 1.0f);
		}
		endRenderText();
	}

	// Render level2 submenu
	if (menuRendererObj.getHorizLevelIndex() == 1 || menuRendererObj.getHorizLevelIndex() == 2)
	{
		int nNoOfSubMenuLevel2Items = ptrSubMenuLevel1->at(activeMenuSelection[1]).size();
		for (int nIndex = 0; nIndex < 3; nIndex++)
		{
			if (nIndex >= (nNoOfSubMenuLevel2Items - 1))
				break;
			beginRenderText(1920, 720);
			{
				string temp = ptrSubMenuLevel1->at(activeMenuSelection[1]).at(nIndex + 1);
				glColor3f(1.0f, 1.0f, 1.0f);
				renderText((float)490 - 20 - 20, (float)170 + (yOffset * nIndex), BITMAP_FONT_TYPE_HELVETICA_18, temp.c_str());
				glColor3f(1.0f, 1.0f, 1.0f);
			}
			endRenderText();
		}
	}
}

void TextOverlayRenderer::keyPressed(int key)
{
	if (key == KEY_LEFT) // left arrow, set selection indexes
	{
		if (2 == menuRendererObj.getHorizLevelIndex())
		{
			activeMenuSelection[1] = 0;
			activeMenuSelection[2] = 0;
		}
		if (1 == menuRendererObj.getHorizLevelIndex())
		{
			activeMenuSelection[1] = 0;
		}

		//OutputDebugStringW(L"Left key.\n");

		// If left (nSelBoxHorizLevelIndex == 0) and top most (selBoxIndex == 0) and menu deisplayed then hide menu. 
		if (DataModel::getInstance()->isMenuDisplayed() 
			&& menuRendererObj.getHorizLevelIndex() == 0 // never called
			) {
			// hide menu. or menu dismissed.
			DataModel::getInstance()->setMenuDisplayStatus(false); // error display is set true later on.
			return;
		}


	}
	if (DataModel::getInstance()->isMenuDisplayed())
	{
		// Main menu movement
		if (0 == menuRendererObj.getHorizLevelIndex())
		{
			int nNoOfMenu = mainMenuItems.size();
			if (key == KEY_DOWN)
			{
				if (menuRendererObj.getSelecedIndex() == 3)
					nMainMenuStartIndex++;
				if ((nMainMenuStartIndex + 3) > (nNoOfMenu))
					nMainMenuStartIndex--;

				activeMenuSelection[menuRendererObj.getHorizLevelIndex()]++;
				if (activeMenuSelection[menuRendererObj.getHorizLevelIndex()] >= nNoOfMenu)
					activeMenuSelection[menuRendererObj.getHorizLevelIndex()]--;
			}
			if (key == KEY_UP)
			{
				if (menuRendererObj.getSelecedIndex() == 1)
					nMainMenuStartIndex--;
				if ((nMainMenuStartIndex) < 0)
					nMainMenuStartIndex++;

				activeMenuSelection[menuRendererObj.getHorizLevelIndex()]--;
				if (activeMenuSelection[menuRendererObj.getHorizLevelIndex()] < 0)
					activeMenuSelection[menuRendererObj.getHorizLevelIndex()]++;
			}
			int nIndex = activeMenuSelection[menuRendererObj.getHorizLevelIndex()];
			ChangeMainMenuWithIndex(nIndex);
		}
		// Sub menu level1 movement
		if (1 == menuRendererObj.getHorizLevelIndex())
		{
			int nNoOfMenu = ptrSubMenuLevel1->size();
			if (key == KEY_DOWN)
			{
				activeMenuSelection[menuRendererObj.getHorizLevelIndex()]++;
				if (activeMenuSelection[menuRendererObj.getHorizLevelIndex()] >= nNoOfMenu)
					activeMenuSelection[menuRendererObj.getHorizLevelIndex()]--;
			}
			if (key == KEY_UP)
			{
				activeMenuSelection[menuRendererObj.getHorizLevelIndex()]--;
				if (activeMenuSelection[menuRendererObj.getHorizLevelIndex()] < 0)
					activeMenuSelection[menuRendererObj.getHorizLevelIndex()]++;
			}
		}
		menuRendererObj.keyPressed(key);
	}
	else
	{
		DataModel::getInstance()->setMenuDisplayStatus(true);
	}
	if (key == 'q'){
		DataModel::getInstance()->setMenuDisplayStatus(false);
	}
}

void TextOverlayRenderer::ChangeMainMenuWithIndex(int index)
{
	if (0 == index)
		ptrSubMenuLevel1 = &subMenu1Level1;
	else if (1 == index)
		ptrSubMenuLevel1 = &subMenu2Level1;
	else if (2 == index)
		ptrSubMenuLevel1 = &subMenu3Level1;
	else if (3 == index)
		ptrSubMenuLevel1 = &subMenu4Level1;
	else if (4 == index)
		ptrSubMenuLevel1 = &subMenu5Level1;
	else if (5 == index)
		ptrSubMenuLevel1 = &subMenu6Level1;
	else if (6 == index)
		ptrSubMenuLevel1 = &subMenu7Level1;
}

void TextOverlayRenderer::setDisplayMode(int mode) {
	this->displayMode = mode;
}

void TextOverlayRenderer::DrawLeftBox() {
	glPushMatrix();
	//glViewport(0, 0, 1920, 720);
	glViewport(30, 77 - 20, 584, 584);

	// Menu
	glColor3f(70.0 / 255.0, 70.0 / 255.0, 70.0 / 255.0);	// White region
	glLineWidth(2.0);
	DrawRectangle(-0.99, 0.3 + .045, 1.97, 0.5 - 0.045); // border

	// Status Display
	glColor3f(70.0 / 255.0, 70.0 / 255.0, 70.0 / 255.0);	// White region
	glLineWidth(2.0);
	DrawRectangle(-0.99, -0.8, 1.97, 1.1); // border

	glPopMatrix();
	glColor3f(1.0, 1.0, 1.0);
}

void TextOverlayRenderer::DrawRectangle(float x, float y, float width, float height) {
	glEnable(GL_LINE_SMOOTH);
	glBegin(GL_LINE_LOOP);
	//glBegin(GL_LINES);
	//glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glVertex2f(x, y);
	glVertex2f(x + width, y);
	glVertex2f(x + width, y + height);
	glVertex2f(x, y + height);
	glVertex2f(x, y);

	glEnd();
}