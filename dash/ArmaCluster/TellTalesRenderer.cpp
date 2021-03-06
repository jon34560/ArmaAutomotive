#include "TellTalesRenderer.h"
#include "DataModel.h"
#include "stb_image.h"

TellTailesRenderer::TellTailesRenderer()
{
}

TellTailesRenderer::~TellTailesRenderer()
{

}

void TellTailesRenderer::initialize()
{
	int width, height, nrChannels;
    
    std::string image_path;
    #ifdef _WIN32
        image_path = ".\\images\\lighton.png";
    #else
        image_path = "./images/lighton.png";
    #endif
	unsigned char *data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &lightOnTexture);
	glBindTexture(GL_TEXTURE_2D, lightOnTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

    #ifdef _WIN32
        image_path = ".\\images\\lightoff.png";
    #else
        image_path = "./images/lightoff.png";
    #endif
	unsigned char *dataLightOff = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &lightOffTexture);
	glBindTexture(GL_TEXTURE_2D, lightOffTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (dataLightOff)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, dataLightOff);
	}
	stbi_image_free(dataLightOff);

	// Hazard
    #ifdef _WIN32
        image_path = ".\\images\\hazardon.png";
    #else
        image_path = "./images/hazardon.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &hazardOnTexture);
	glBindTexture(GL_TEXTURE_2D, hazardOnTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

    #ifdef _WIN32
        image_path = ".\\images\\hazardoff.png";
    #else
        image_path = "./images/hazardoff.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &hazardOffTexture);
	glBindTexture(GL_TEXTURE_2D, hazardOffTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Park
    #ifdef _WIN32
        image_path = ".\\images\\parkon.png";
    #else
        image_path = "./images/parkon.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &parkOnTexture);
	glBindTexture(GL_TEXTURE_2D, parkOnTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

    #ifdef _WIN32
        image_path = ".\\images\\parkoff.png";
    #else
        image_path = "./images/parkoff.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &parkOffTexture);
	glBindTexture(GL_TEXTURE_2D, parkOffTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Right turn
    #ifdef _WIN32
        image_path = ".\\images\\right_arrow.png";
    #else
        image_path = "./images/right_arrow.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &rightOnTexture);
	glBindTexture(GL_TEXTURE_2D, rightOnTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

    #ifdef _WIN32
        image_path = ".\\images\\right_arrow_off.png";
    #else
        image_path = "./images/right_arrow_off.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &rightOffTexture);
	glBindTexture(GL_TEXTURE_2D, rightOffTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Left turn
    #ifdef _WIN32
        image_path = ".\\images\\left_arrow.png";
    #else
        image_path = "./images/left_arrow.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &leftOnTexture);
	glBindTexture(GL_TEXTURE_2D, leftOnTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

    #ifdef _WIN32
        image_path = ".\\images\\left_arrow_off.png";
    #else
        image_path = "./images/left_arrow_off.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &leftOffTexture);
	glBindTexture(GL_TEXTURE_2D, leftOffTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	// Check
    #ifdef _WIN32
        image_path = ".\\images\\checkon.png";
    #else
        image_path = "./images/checkon.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &checkOnTexture);
	glBindTexture(GL_TEXTURE_2D, checkOnTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

    #ifdef _WIN32
        image_path = ".\\images\\checkoff.png";
    #else
        image_path = "./images/checkoff.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &checkOffTexture);
	glBindTexture(GL_TEXTURE_2D, checkOffTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);


	// Seatbelt
    #ifdef _WIN32
        image_path = ".\\images\\seatbelt_on.png";
    #else
        image_path = "./images/seatbelt_on.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &seatbeltOnTexture);
	glBindTexture(GL_TEXTURE_2D, seatbeltOnTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

    #ifdef _WIN32
        image_path = ".\\images\\seatbelt_off.png";
    #else
        image_path = "./images/seatbelt_off.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &seatbeltOffTexture);
	glBindTexture(GL_TEXTURE_2D, seatbeltOffTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

	

	// Door Ajar
    #ifdef _WIN32
        image_path = ".\\images\\door_ajar_on.png";
    #else
        image_path = "./images/door_ajar_on.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &doorAjarOnTexture);
	glBindTexture(GL_TEXTURE_2D, doorAjarOnTexture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	stbi_image_free(data);

    #ifdef _WIN32
        image_path = ".\\images\\door_ajar_off.png";
    #else
        image_path = "./images/door_ajar_off.png";
    #endif
	data = stbi_load(image_path.c_str(), &width, &height, &nrChannels, 0);

	glGenTextures(1, &doorAjarOffTexture);
	glBindTexture(GL_TEXTURE_2D, doorAjarOffTexture);
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

void TellTailesRenderer::doRender()
{
	glPushMatrix();

	glEnable(GL_TEXTURE_2D);
	glRotatef(180.0, 1, 0, 0);
	

	// Parking telltale
	glViewport(1318 + 30, 632, 75, 50);
	if (DataModel::getInstance()->getparkingTellTaleStatus())
		glBindTexture(GL_TEXTURE_2D, parkOnTexture);
	else
		glBindTexture(GL_TEXTURE_2D, parkOffTexture);
	drawTellTale();

	// Light telltale
	glViewport(1726 + 15, 630, 72, 53);

	if(DataModel::getInstance()->getlightTellTaleStatus())
		glBindTexture(GL_TEXTURE_2D, lightOnTexture);
	else
		glBindTexture(GL_TEXTURE_2D, lightOffTexture);
	drawTellTale();

	// Hazard telltale
	glViewport(1524 + 25, 632, 65, 56);
	if (DataModel::getInstance()->gethazardTellTaleStatus())
		glBindTexture(GL_TEXTURE_2D, hazardOnTexture);
	else
		glBindTexture(GL_TEXTURE_2D, hazardOffTexture);
	drawTellTale();


	// Right turn telltale
	glViewport(1160 + 10, 622 - 36, 77, 73); // 89, 73 
	if (DataModel::getInstance()->getrightTurnTellTaleStatus())
		glBindTexture(GL_TEXTURE_2D, rightOnTexture);
	else
		glBindTexture(GL_TEXTURE_2D, rightOffTexture);
	drawTellTale();

	// Left turn telltale
	glViewport(670 - 4, 622 - 36, 77, 73); // 89, 73 
	if (DataModel::getInstance()->getleftTurnTellTaleStatus())
		glBindTexture(GL_TEXTURE_2D, leftOnTexture);
	else
		glBindTexture(GL_TEXTURE_2D, leftOffTexture);
	drawTellTale();


	// door ajar telltale
	glViewport(296 - 200, 632, 50, 56);
	if (DataModel::getInstance()->getvehicleCheckTellTaleStatus()) {
		glBindTexture(GL_TEXTURE_2D, doorAjarOnTexture);
		//else
			//glBindTexture(GL_TEXTURE_2D, doorAjarOffTexture);
		drawTellTale();
	}


	// vehicle check telltale
	glViewport(296 - 10, 632, 66, 50);
	if (DataModel::getInstance()->getvehicleCheckTellTaleStatus())
		glBindTexture(GL_TEXTURE_2D, checkOnTexture);
	else
		glBindTexture(GL_TEXTURE_2D, checkOffTexture);
	drawTellTale();


	// seatbelt telltale
	glViewport(296 + 200, 632, 34, 56);
	if (DataModel::getInstance()->getvehicleCheckTellTaleStatus())
		glBindTexture(GL_TEXTURE_2D, seatbeltOnTexture);
	else
		glBindTexture(GL_TEXTURE_2D, seatbeltOffTexture);
	drawTellTale();



	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void TellTailesRenderer::drawTellTale()
{
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
}

void TellTailesRenderer::keyPressed(int key)
{

}
