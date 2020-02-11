#pragma once
#include <GL/glew.h>
#include <GL/freeglut.h>
#include <GL/glaux.h>

class ObjectRendererBase
{
private:
	
public:
	ObjectRendererBase();
	~ObjectRendererBase();
	virtual void initialize() = 0;
	virtual void doRender() = 0;
	virtual void keyPressed(int key)=0;
};
