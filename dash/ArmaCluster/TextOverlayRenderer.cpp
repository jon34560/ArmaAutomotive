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
		char const* fuelValchar = f.c_str();

		int temperatureVal = DataModel::getInstance()->gettemperatureValue();
		const char char_temperaturemax = -1;
		std::string t = std::to_string(temperatureVal);
		char const* temperatureValchar = t.c_str();

		int rangeVal = DataModel::getInstance()->getrangeValue();
		const char char_rangemax = -1;
		std::string r = std::to_string(rangeVal);
		char const* rangeValchar = r.c_str();

		int pressureVal = DataModel::getInstance()->getpressureValue();
		const char char_pressuremax = -1;
		std::string p = std::to_string(pressureVal);
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
		const char char_timemax = -1;
		char const* timeValchar = timeVal.c_str();

		renderText(1400, 223, BITMAP_FONT_TYPE_HELVETICA_18, fuelValchar ); // Fuel  BITMAP_FONT_TYPE_TIMES_ROMAN_24
		renderText(1650, 223, BITMAP_FONT_TYPE_HELVETICA_18, rangeValchar); // Range
		renderText(1405, 467, BITMAP_FONT_TYPE_HELVETICA_18, temperatureValchar); // Temp
		renderText(1655, 467, BITMAP_FONT_TYPE_HELVETICA_18, pressureValchar); // Oil pressure

		renderText(1557, 680, BITMAP_FONT_TYPE_HELVETICA_18, voltageValchar); // voltage
		renderText(1770, 684, BITMAP_FONT_TYPE_HELVETICA_18, odometerValchar); // odo
		renderText(1300, 684, BITMAP_FONT_TYPE_HELVETICA_18, timeValchar); // time

		// Speed units
		renderText(940, 433, BITMAP_FONT_TYPE_HELVETICA_18, "Kmh"); // Speed Units
		// Helvetica18 BITMAP_FONT_TYPE_TIMES_ROMAN_24


		glColor3f(1.0f, 1.0f, 1.0f);
	}
	endRenderText();

	if (DataModel::getInstance()->isMenuDisplayNeeded())
	{
		menuRendererObj.setSubMenuMaxLimits(ptrSubMenuLevel1->size(), ptrSubMenuLevel1->at(activeMenuSelection[1]).size() - 1);
		drawMainMenu();
		menuRendererObj.doRender();
	}
	else
	{
		// Just print settings
		beginRenderText(1920, 720);
		{
			glColor3f(1.0f, 1.0f, 1.0f);
			renderText(80, 170 , BITMAP_FONT_TYPE_HELVETICA_18,"Settings");
			glColor3f(1.0f, 1.0f, 1.0f);
		}
		endRenderText();
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
	int yOffset = 45;
	int nNoOfMenu = mainMenuItems.size();
	for (int nIndex = 0; nIndex < 3; nIndex++)
	{
		int menuStartIndex = nMainMenuStartIndex+ nIndex;
		if (menuStartIndex >= (nNoOfMenu))
			break;
		beginRenderText(1920, 720);
		{
		glColor3f(1.0f, 1.0f, 1.0f);
		renderText((float)80, (float)170 + (yOffset * nIndex), BITMAP_FONT_TYPE_HELVETICA_18, mainMenuItems.at(menuStartIndex).c_str());
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
			renderText((float)290, (float)170 + (yOffset * nIndex), BITMAP_FONT_TYPE_HELVETICA_18, temp.c_str());
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
				renderText((float)490, (float)170 + (yOffset * nIndex), BITMAP_FONT_TYPE_HELVETICA_18, temp.c_str());
				glColor3f(1.0f, 1.0f, 1.0f);
			}
			endRenderText();
		}
	}
}

void TextOverlayRenderer::keyPressed(int key)
{
	if (key == KEY_LEFT)
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
	}
	if (DataModel::getInstance()->isMenuDisplayNeeded())
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