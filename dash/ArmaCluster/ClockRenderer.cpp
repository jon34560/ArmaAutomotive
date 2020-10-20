#include "ClockRenderer.h"
#include "DataModel.h"
#include "stb_image.h"
#include <iostream>
#include <chrono>
#include <ctime>  

ClockRenderer::ClockRenderer()
{
	
}

ClockRenderer::~ClockRenderer()
{

}

void ClockRenderer::initialize()
{
	int width, height, nrChannels;

}


void ClockRenderer::doRender()
{
	glPushMatrix();

	glEnable(GL_TEXTURE_2D);
	glRotatef(180.0, 1, 0, 0);


	drawClock();


	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void ClockRenderer::drawTellTale()
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

void ClockRenderer::keyPressed(int key)
{

}

void ClockRenderer::drawClock() {
	glPushMatrix();
	//glViewport(0, 0, 1920, 720);
	glViewport(66, 77 - 6, 584, 584);
	//drawBackground();

	float radius = 0.45; // 0.739;

	

	// Hour markers
	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, -1, 1);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, 29, 31); // 

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, 59, 61);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, 89, 91);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, 119, 121);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, 149, 151);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, 179, 181);
	
	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, 209, 211);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, 239, 241);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, 269, 271);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, 299, 301);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(6.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.185, 720, 329, 331);


	// Inner circle (dark grey shadow)
	glColor3f(60.0 / 255.0, 60.0 / 255.0, 60.0 / 255.0);	// White region
	glLineWidth(3.0);
	//rpmRenderer.DrawArcSegment(0.0, 0.0, radius + 0.210, 360, 0, 361);
	rpmRenderer.DrawCircle(0.0, 0.0, radius + 0.2090, 120);

	// Outer Circle
	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(2.0);
	rpmRenderer.DrawCircle(0.0, 0.0, radius + 0.2, 120);

	glColor3f(240.0 / 255.0, 240.0 / 255.0, 240.0 / 255.0);
	glLineWidth(2.0);
	rpmRenderer.DrawCircle(0.0, 0.0, radius + 0.196, 120);


	std::string timeVal = DataModel::getInstance()->gettimeValue();
	//auto time = std::chrono::system_clock::now();
	std::time_t tm = std::time(0);   // get time now
	std::tm* now = std::localtime(&tm);

	const char char_timemax = -1;
	std::string m = "AM";
	int hour = now->tm_hour;
	if (hour > 12) {
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
	//std::string xxx;
	//xxx = std::to_string(hour) + " : " + minute + " : " + second + " " + m;


	// second hand
	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(36.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius - 0.15, 720,  (now->tm_sec * 6) - 1 , (now->tm_sec * 6) + 1 );

	// Minute hand
	glColor3f(180.0 / 255.0, 180.0 / 255.0, 180.0 / 255.0);
	glLineWidth(26.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius - 0.25, 720, (now->tm_min * 6) - 1, (now->tm_min * 6) + 1);

	// Hour hand
	glColor3f(180.0 / 255.0, 180.0 / 255.0, 180.0 / 255.0);
	glLineWidth(26.0);
	rpmRenderer.DrawArcSegment(0.0, 0.0, radius - 0.35, 720, (now->tm_hour * 6) - 1, (now->tm_hour * 6) + 1);


	// Draw gauge
	/*
	glLineWidth(12.0);
	if (angle < 270) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0); }
	else if (angle < 315) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }
	else if (angle > 315) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); }
	rpmRenderer.DrawArc(0.0, 0.0, radius, 360, angle);
	/*

	// Inner shadow
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	rpmRenderer.DrawArc(0.0, 0.0, radius - 0.007, 360, angle);
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }
	rpmRenderer.DrawArc(0.0, 0.0, radius - 0.013, 360, angle);
	// Outer shadow
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	rpmRenderer.DrawArc(0.0, 0.0, radius + 0.007, 360, angle);
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }
	rpmRenderer.DrawArc(0.0, 0.0, radius + 0.013, 360, angle);


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
	//glEnable(GL_TEXTURE_2D);
	//glViewport(667, 77, 584, 584);

	//glEnable(GL_TEXTURE_2D);
	//glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	//glEnable(GL_BLEND);
	//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	/*
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
	*/
	if (reverse == false) {
		angle += 2;
	}
	else {
		angle -= 2;
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

/*
void ClockRenderer::DrawArcSegment(float cx, float cy, float r, int num_segments, int start_angle, int end_angle) {
	int start = (int)((float)num_segments * ((float)start_angle / (float)360));
	int arc = (int)((float)num_segments * ((float)end_angle / (float)360));
	int quarter_segments = (int)((float)num_segments * 0.25);
	glEnable(GL_LINE_SMOOTH);
	glBegin(GL_LINES);
	float prevX = 0;
	float prevY = 0;
	for (int ii = start; ii < num_segments && ii < arc; ii++) { // - (num_segments/4)
		float theta = (2.0f * 3.1415926f) * ((float(ii + quarter_segments) / float(num_segments))); //
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

void ClockRenderer::DrawCircle(float cx, float cy, float r, int num_segments) {
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


void ClockRenderer::DrawArc(float cx, float cy, float r, int num_segments, int arc_angle) {
	int arc = (int)((float)num_segments * ((float)arc_angle / (float)360));
	int quarter_segments = (int)((float)num_segments * 0.25);
	glEnable(GL_LINE_SMOOTH);
	glBegin(GL_LINES);
	float prevX = 0;
	float prevY = 0;
	for (int ii = 0; ii < num_segments && ii < arc; ii++) { // - (num_segments/4)
		float theta = (2.0f * 3.1415926f) * ((float(ii + quarter_segments) / float(num_segments))); //
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

void ClockRenderer::DrawArcSegment(float cx, float cy, float r, int num_segments, int start_angle, int end_angle) {
	int start = (int)((float)num_segments * ((float)start_angle / (float)360));
	int arc = (int)((float)num_segments * ((float)end_angle / (float)360));
	int quarter_segments = (int)((float)num_segments * 0.25);
	glEnable(GL_LINE_SMOOTH);
	glBegin(GL_LINES);
	float prevX = 0;
	float prevY = 0;
	for (int ii = start; ii < num_segments && ii < arc; ii++) { // - (num_segments/4)
		float theta = (2.0f * 3.1415926f) * ((float(ii + quarter_segments) / float(num_segments))); //
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
*/