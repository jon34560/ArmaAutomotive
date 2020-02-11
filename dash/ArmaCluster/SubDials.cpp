#include "SubDials.h"
#include "stb_image.h"
#include "DataModel.h"


SubDials::SubDials()
{
	angle = 0;
}

SubDials::~SubDials()
{

}

void SubDials::initialize()
{
	m_ply_temp = new c_meter_polygenerator(200.0f, 200.0f, 200.0f, 270.0f, 400, 400);

	int width, height, nrChannels;
	unsigned char *data = stbi_load(".\\images\\circle.png", &width, &height, &nrChannels, 0);

	glGenTextures(1, &rpmMeterFrame);

	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

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
	glEnable(GL_TEXTURE_2D);
}

void SubDials::doRender()
{
	drawFuelDial();
	drawRangeDial();
	drawTempDial();
	drawOilPresDial();
}

/**
* drawFuelDial
*
*/ 
void SubDials::drawFuelDial()
{
	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(1341, 397, 189, 189);

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	int fuelDialVal = DataModel::getInstance()->getfuelDialAngle();
	int s32_count = 0;
	std::vector<Triangle> TrianglesVe;
	TrianglesVe = m_ply_temp->get_traignles_for_rendering(-1 * fuelDialVal);

	for (s32_count = 0; s32_count < TrianglesVe.size(); s32_count++)
	{
		glBegin(GL_TRIANGLES);
		glColor3f(216.0/255.0, 34.0/256.0, 0.0);

		glTexCoord2f((TrianglesVe[s32_count].uv1[0]), 1.0 - TrianglesVe[s32_count].uv1[1]);
		glVertex2f(TrianglesVe[s32_count].point1[0], TrianglesVe[s32_count].point1[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv3[0]), 1.0 - TrianglesVe[s32_count].uv3[1]);
		glVertex2f((TrianglesVe[s32_count].point3[0]), TrianglesVe[s32_count].point3[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv2[0]), 1.0 - TrianglesVe[s32_count].uv2[1]);
		glVertex2f((TrianglesVe[s32_count].point2[0]), TrianglesVe[s32_count].point2[1]);

		glEnd();
	}
	glColor3f(1.0, 1.0, 1.0);
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void SubDials::drawRangeDial()
{
	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(1593, 397, 189, 189);

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	int rangeDialVal = DataModel::getInstance()->getrangeDialAngle();
	int s32_count = 0;
	std::vector<Triangle> TrianglesVe;
	TrianglesVe = m_ply_temp->get_traignles_for_rendering(-1 * rangeDialVal);

	for (s32_count = 0; s32_count < TrianglesVe.size(); s32_count++)
	{
		glBegin(GL_TRIANGLES);
		glColor3f(12.0 / 256.0, 218.0 / 256.0, 0.0);

		glTexCoord2f((TrianglesVe[s32_count].uv1[0]), 1.0 - TrianglesVe[s32_count].uv1[1]);
		glVertex2f(TrianglesVe[s32_count].point1[0], TrianglesVe[s32_count].point1[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv3[0]), 1.0 - TrianglesVe[s32_count].uv3[1]);
		glVertex2f((TrianglesVe[s32_count].point3[0]), TrianglesVe[s32_count].point3[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv2[0]), 1.0 - TrianglesVe[s32_count].uv2[1]);
		glVertex2f((TrianglesVe[s32_count].point2[0]), TrianglesVe[s32_count].point2[1]);

		glEnd();
	}
	glColor3f(1.0, 1.0, 1.0);
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void SubDials::drawTempDial()
{
	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(1341, 152, 189, 189);

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	int temperatureDialVal = DataModel::getInstance()->gettemperatureDialAngle();
	int s32_count = 0;
	std::vector<Triangle> TrianglesVe;
	TrianglesVe = m_ply_temp->get_traignles_for_rendering(-1 * temperatureDialVal);

	for (s32_count = 0; s32_count < TrianglesVe.size(); s32_count++)
	{
		glBegin(GL_TRIANGLES);
		glColor3f(216.0 / 255.0, 34.0 / 256.0, 0.0);

		glTexCoord2f((TrianglesVe[s32_count].uv1[0]), 1.0 - TrianglesVe[s32_count].uv1[1]);
		glVertex2f(TrianglesVe[s32_count].point1[0], TrianglesVe[s32_count].point1[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv3[0]), 1.0 - TrianglesVe[s32_count].uv3[1]);
		glVertex2f((TrianglesVe[s32_count].point3[0]), TrianglesVe[s32_count].point3[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv2[0]), 1.0 - TrianglesVe[s32_count].uv2[1]);
		glVertex2f((TrianglesVe[s32_count].point2[0]), TrianglesVe[s32_count].point2[1]);

		glEnd();
	}
	glColor3f(1.0, 1.0, 1.0);
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void SubDials::drawOilPresDial()
{
	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(1593, 152, 189, 189);

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	int pressureDialVal = DataModel::getInstance()->getpressureDialAngle();
	int s32_count = 0;
	std::vector<Triangle> TrianglesVe;
	TrianglesVe = m_ply_temp->get_traignles_for_rendering(-1 * pressureDialVal);

	for (s32_count = 0; s32_count < TrianglesVe.size(); s32_count++)
	{
		glBegin(GL_TRIANGLES);
		glColor3f(254.0 / 255.0, 255.0 / 256.0, 14.0 / 256.0);

		glTexCoord2f((TrianglesVe[s32_count].uv1[0]), 1.0 - TrianglesVe[s32_count].uv1[1]);
		glVertex2f(TrianglesVe[s32_count].point1[0], TrianglesVe[s32_count].point1[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv3[0]), 1.0 - TrianglesVe[s32_count].uv3[1]);
		glVertex2f((TrianglesVe[s32_count].point3[0]), TrianglesVe[s32_count].point3[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv2[0]), 1.0 - TrianglesVe[s32_count].uv2[1]);
		glVertex2f((TrianglesVe[s32_count].point2[0]), TrianglesVe[s32_count].point2[1]);

		glEnd();
	}
	glColor3f(1.0, 1.0, 1.0);
	angle++;
	if (angle > 360)
		angle = 0;
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void SubDials::keyPressed(int key)
{

}