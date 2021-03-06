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
* DrawArc
* Description:
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

void RpmRenderer::DrawArcSegment(float cx, float cy, float r, int num_segments, int start_angle, int end_angle) {
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

// DEPRICATE
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
	if (displayMode == 1) {
		dialA();
	}
	else {
		dialB();
	}
}

void RpmRenderer::dialA(){
	glPushMatrix();
	//glViewport(0, 0, 1920, 720);
	glViewport(667, 77 - 6, 584, 584);
	//drawBackground();
	
	float radius = 0.739;

	// Draw unit line
	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(4.0);
	DrawArcSegment(0.0, 0.0, radius, 720, 60, 62);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(4.0);
	DrawArcSegment(0.0, 0.0, radius, 720, 120, 122);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(4.0);
	DrawArcSegment(0.0, 0.0, radius, 720, 180, 182);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(4.0);
	DrawArcSegment(0.0, 0.0, radius, 720, 240, 242);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(4.0);
	DrawArcSegment(0.0, 0.0, radius, 720, 300, 302);
	

	// Inner circle
	glColor3f(60.0 / 255.0, 60.0 / 255.0, 60.0 / 255.0);	// White region
	glLineWidth(2.0);
	//DrawCircle(0.0, 0.0, 0.739, 120);
	DrawArcSegment(0.0, 0.0, 0.739, 360, 0, 271);

	glColor4f(225.0 / 255.0, 225.0 / 255.0, 0.0 / 255.0, 0.5); // yellow region
	glLineWidth(2.0);
	DrawArcSegment(0.0, 0.0, radius, 360, 270, 316);

	glColor4f(225.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.5); // red region
	glLineWidth(2.0);
	DrawArcSegment(0.0, 0.0, radius, 360, 315, 360);

	// Outer Circle
	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(2.0);
	DrawCircle(0.0, 0.0, 0.886, 120);

	
	// Draw gauge
	glLineWidth(12.0);
	if(angle < 270){ glColor3f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0); } 
	else if(angle < 315){ glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }
	else if (angle > 315) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); }
	DrawArc(0.0, 0.0, radius, 360, angle);

	// Inner shadow
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	DrawArc(0.0, 0.0, radius - 0.007, 360, angle);
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }
	DrawArc(0.0, 0.0, radius - 0.013, 360, angle);
	// Outer shadow
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	DrawArc(0.0, 0.0, radius + 0.007, 360, angle);
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }
	DrawArc(0.0, 0.0, radius + 0.013, 360, angle);


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

void RpmRenderer::dialB() {
	glPushMatrix();
	//glViewport(0, 0, 1920, 720);
	glViewport(667, 77 - 6, 584, 584);
	//drawBackground();

	float radius = 0.739;

	// Inner background white
	glLineWidth(12.0);
	glColor3f(15.0 / 255.0, 15.0 / 255.0, 15.0 / 255.0);
	DrawArc(0.0, 0.0, radius + 0.014, 360, 271);
	DrawArc(0.0, 0.0, radius + 0.045, 360, 271);
	DrawArc(0.0, 0.0, radius + 0.078, 360, 271);
	DrawArc(0.0, 0.0, radius + 0.102, 360, 271);
	DrawArc(0.0, 0.0, radius + 0.134, 360, 271);

	// Inner background yellow
	glColor3f(25.0 / 255.0, 25.0 / 255.0, 0.0 / 255.0); // yellow region
	glLineWidth(12.0);
	DrawArcSegment(0.0, 0.0, radius + 0.014, 360, 270, 316);
	DrawArcSegment(0.0, 0.0, radius + 0.045, 360, 270, 316);
	DrawArcSegment(0.0, 0.0, radius + 0.078, 360, 270, 316);
	DrawArcSegment(0.0, 0.0, radius + 0.102, 360, 270, 316);
	DrawArcSegment(0.0, 0.0, radius + 0.134, 360, 270, 316);

	// Inner background red segment
	glColor3f(25.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); // yellow region
	glLineWidth(12.0);
	DrawArcSegment(0.0, 0.0, radius + 0.014, 360, 315, 360);
	DrawArcSegment(0.0, 0.0, radius + 0.045, 360, 315, 360);
	DrawArcSegment(0.0, 0.0, radius + 0.078, 360, 315, 360);
	DrawArcSegment(0.0, 0.0, radius + 0.102, 360, 315, 360);
	DrawArcSegment(0.0, 0.0, radius + 0.134, 360, 315, 360);


	// Inner circle
	glColor3f(60.0 / 255.0, 60.0 / 255.0, 60.0 / 255.0);	// White region
	glLineWidth(2.0);
	DrawArcSegment(0.0, 0.0, 0.739, 360, 0, 271);



	// Draw unit segment line
	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(4.0);
	DrawArcSegment(0.0, 0.0, radius, 720, 60, 62);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(4.0);
	DrawArcSegment(0.0, 0.0, radius, 720, 120, 122);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(4.0);
	DrawArcSegment(0.0, 0.0, radius, 720, 180, 182);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(4.0);
	DrawArcSegment(0.0, 0.0, radius, 720, 240, 242);

	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(4.0);
	DrawArcSegment(0.0, 0.0, radius, 720, 300, 302);


	// 
	glColor4f(225.0 / 255.0, 225.0 / 255.0, 0.0 / 255.0, 0.5); // yellow region
	glLineWidth(2.0);
	DrawArcSegment(0.0, 0.0, radius, 360, 270, 316);

	glColor4f(225.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.5); // red region
	glLineWidth(2.0);
	DrawArcSegment(0.0, 0.0, radius, 360, 315, 360);

	// Outer Circle
	glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glLineWidth(2.0);
	DrawCircle(0.0, 0.0, 0.886, 120);


	// Draw gauge
	glLineWidth(12.0);
	if (angle < 270) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0); }
	else if (angle < 315) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }
	else if (angle > 315) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); }
	DrawArc(0.0, 0.0, radius + 0.014, 360, angle);
	DrawArc(0.0, 0.0, radius + 0.045, 360, angle);
	DrawArc(0.0, 0.0, radius + 0.078, 360, angle);
	DrawArc(0.0, 0.0, radius + 0.102, 360, angle);
	DrawArc(0.0, 0.0, radius + 0.134, 360, angle);

	
	// Inner shadow
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	DrawArc(0.0, 0.0, radius + 0.012 - 0.007, 360, angle);
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }
	DrawArc(0.0, 0.0, radius + 0.012 - 0.013, 360, angle);
	// Outer shadow
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	DrawArc(0.0, 0.0, radius + 0.002 + 0.134 + 0.007, 360, angle);
	if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); }
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }
	DrawArc(0.0, 0.0, radius + 0.002 + 0.134 + 0.013, 360, angle);
	


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
	
	if (reverse == false) {
		angle += 2;
		//angle += 1;
	}
	else {
		angle -= 2;
		//angle -= 1;
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

void RpmRenderer::setDisplayMode(int mode) {
	this->displayMode = mode;
}