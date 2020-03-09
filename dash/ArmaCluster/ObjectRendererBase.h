#pragma once
#include <GL/glew.h>
#include <GL/freeglut.h>

#ifdef _WIN32
#include <GL/glaux.h> // What is this for?
#endif

class ObjectRendererBase
{
private:
	
public:
	ObjectRendererBase();
	~ObjectRendererBase();
	virtual void initialize() = 0;
	virtual void doRender() = 0;
	virtual void keyPressed(int key)=0;
	virtual void setDisplayMode(int mode);
};
