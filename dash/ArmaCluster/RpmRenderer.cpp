#include "RpmRenderer.h"
#ifndef STB_IMAGE_IMPLEMENTATION
#define STB_IMAGE_IMPLEMENTATION
#endif // !STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

RpmRenderer::RpmRenderer()
{
	angle = 0;
	reverse = false;
}

RpmRenderer::~RpmRenderer()
{

}

void RpmRenderer::initialize()
{
	m_ply_temp = new c_meter_polygenerator(200.0f, 200.0f, 200.0f, 270.0f, 400, 400);

	int width, height, nrChannels;
	unsigned char *data = stbi_load(".\\images\\rpmfilled.png", &width, &height, &nrChannels, 0);

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

void RpmRenderer::drawFilledSun() {
	//static float angle;
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glLoadIdentity();
	glTranslatef(0, 0, -10);
	int i, x, y;
	double radius = 0.30;
	//glColor3ub(253, 184, 19);     
	glColor3ub(255, 0, 0);
	double twicePi = 2.0 * 3.142;
	x = 0, y = 0;
	glBegin(GL_TRIANGLE_FAN); //BEGIN CIRCLE
	glVertex2f(x, y); // center of circle
	for (i = 0; i <= 20; i++) {
		glVertex2f(
			(x + (radius * cos(i * twicePi / 20))), (y + (radius * sin(i * twicePi / 20)))
		);
	}
	glEnd(); //END
}

void RpmRenderer::DrawCircle(float cx, float cy, float r, int num_segments) {
	glEnable(GL_LINE_SMOOTH);
	glBegin(GL_LINE_LOOP);
	//glBegin(GL_LINES);
	//glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	for (int ii = 0; ii < num_segments; ii++) {
		float theta = 2.0f * 3.1415926f * float(ii) / float(num_segments); //get the current angle 
		float x = r * cosf(theta); //calculate the x component 
		float y = r * sinf(theta); //calculate the y component 
		glVertex2f(x + cx, y + cy); //output vertex 
	}
	glEnd();
}

/**
*
*/
void RpmRenderer::DrawArc(float cx, float cy, float r, int num_segments, int arc_angle) {
	int arc = (int)((float)num_segments * ((float)arc_angle / (float)360));
	int quarter_segments = (int)((float)num_segments * 0.25);
	glEnable(GL_LINE_SMOOTH);
	glBegin(GL_LINES);
	float prevX = 0;
	float prevY = 0;
	for (int ii = 0; ii < num_segments && ii < arc ; ii++) { // - (num_segments/4)
		float theta = (2.0f * 3.1415926f) * ((float(ii + quarter_segments) / float(num_segments) ) ); //
		theta = -theta;
		float x = r * cosf(theta); //calculate the x component 
		float y = r * sinf(theta); //calculate the y component 
		glVertex2f(x + cx, y + cy); //output vertex 

		if (prevX != 0 && prevY != 0) {
			glVertex2f(prevX + cx, prevY + cy);
		}
		prevX = x;
		prevY = y;
	}
	glEnd();
}

void RpmRenderer::drawBackground()
{
	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(701, 200, 450, 450); // x, y, w, h

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	//glEnable(GL_BLEND);
	//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	int fuelDialVal = 300; //  DataModel::getInstance()->getfuelDialAngle();
	int s32_count = 0;
	std::vector<Triangle> TrianglesVe;
	TrianglesVe = m_ply_temp->get_traignles_for_rendering(-1 * fuelDialVal);

	for (s32_count = 0; s32_count < TrianglesVe.size(); s32_count++)
	{
		glBegin(GL_TRIANGLES);
		glColor3f(166.0 / 255.0, 166.0 / 255.0, 166.0 / 255.0);

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

/**
* doRender
* Description: 
*/
void RpmRenderer::doRender()
{
	glPushMatrix();
	//glViewport(0, 0, 1920, 720);
	glViewport(667, 77, 584, 584);
	//drawBackground();

	// Inner circle
	glColor3f(60.0 / 255.0, 60.0 / 255.0, 60.0 / 255.0);
	glLineWidth(2.0);
	DrawCircle(0.0, 0.0, 0.739, 120);

	// Outer Circle
	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(2.0);
	DrawCircle(0.0, 0.0, 0.886, 120);

	glLineWidth(12.0);
	//int deg = angle / 3.6;
	DrawArc(0.0, 0.0, 0.586, 360, angle);

	//glEnable(GL_BLEND);
	//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);



	/*
	glLineWidth(1.0);
	glBegin(GL_LINES);				//
	//glColor3f(1.0f, 0.5f, 0.5f);
	glVertex2f(0, 0);
	glVertex2f(0.1, 0.1);
	glVertex2f(-0.1, -0.3);
	glEnd();
		*/
		/*
		glBegin(GL_POLYGON);			// Works
		glColor3f(1.0f, 0.0f, 0.0f);
		glVertex2f(-0.5, -0.5);
		glColor3f(1.0f, 0.0f, 1.0f);
		glVertex2f(-0.5, 0.5);
		glColor3f(0.0f, 1.0f, 0.0f);
		glVertex2f(0.5, 0.5);
		glColor3f(1.0f, 0.0f, 0.0f);
		glVertex2f(0.5, -0.5);
		glEnd();
		*/



	glPopMatrix();

	glColor3f(1.0, 1.0, 1.0);



	// Draw RPM Dial
	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(667, 77, 584, 584);

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	int s32_count = 0;
	std::vector<Triangle> TrianglesVe;
	TrianglesVe = m_ply_temp->get_traignles_for_rendering(-angle);

	for (s32_count = 0; s32_count < TrianglesVe.size(); s32_count++)
	{
		glBegin(GL_TRIANGLES);
		if (angle > 315)
			glColor3f(1.0, 0.0, 0.0);
		else if (angle > 270)
			glColor3f(1.0, 1.0, 0.0);


		glTexCoord2f((TrianglesVe[s32_count].uv1[0]), 1.0 - TrianglesVe[s32_count].uv1[1]);
		glVertex2f(TrianglesVe[s32_count].point1[0], TrianglesVe[s32_count].point1[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv3[0]), 1.0 - TrianglesVe[s32_count].uv3[1]);
		glVertex2f((TrianglesVe[s32_count].point3[0]), TrianglesVe[s32_count].point3[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv2[0]), 1.0 - TrianglesVe[s32_count].uv2[1]);
		glVertex2f((TrianglesVe[s32_count].point2[0]), TrianglesVe[s32_count].point2[1]);

		glEnd();
	}
	glColor3f(1.0, 1.0, 1.0);
	if (reverse == false) {
		angle+=2;
	}
	else {
		angle-=2;
	}
	if (angle > 360) {
		reverse = true;
		//angle = 0;
	}
	if (angle <= 0) {
		reverse = false;
	}
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
	
}

void RpmRenderer::keyPressed(int key)
{

}