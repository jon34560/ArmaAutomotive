CC_LINUX=g++
CC_MAC=clang++
OUT_PATH=./bin
SRC_PATH_MAC= -I ./ *.cpp -I./GL_Dependency/include -I/usr/local/include   
SRC_PATH_LINUX= -I ./ *.cpp -I/usr/include/openssl -I/usr/local/include  
FILES_MAC= ./   
FILES_LINUX= ./
#  `pkg-config --cflags --libs nice`
#CFLAGS_MAC= -L ./GL_Dependency/lib -L/usr/local/lib -framework OpenGL -framework GLUT  -lz -std=c++11 -stdlib=libc++ -Wdeprecated -Wc++98-compat -w  
CFLAGS_MAC= -L./GL_Dependency/lib -L/usr/local/lib -framework OpenGL -framework GLUT   -std=c++11 -stdlib=libc++ -Wdeprecated  -Wc++98-compat -w  
CFLAGS_LINUX= -L./GL_Dependency/lib -L/usr/local/lib/ -framework OpenGL -framework GLUT   -std=c++11 -stdlib=libc++ -Wdeprecated  -Wc++98-compat -w 

# ${FILES_MAC}

all:
	mkdir -p ${OUT_PATH}
	${CC_MAC} ${CFLAGS_MAC} ${SRC_PATH_MAC} -o ${OUT_PATH}/Dash

mac:
	mkdir -p ${OUT_PATH}
	${CC_MAC} ${CFLAGS_MAC} ${SRC_PATH_MAC} -o ${OUT_PATH}/Dash.app

linux:
	mkdir -p ${OUT_PATH}
	${CC_MAC} ${CFLAGS_LINUX} -std=c++11 ${SRC_PATH_LINUX} ${FILES_LINUX} -o ${OUT_PATH}/Dash



