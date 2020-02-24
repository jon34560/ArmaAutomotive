CC_LINUX=g++
CC_MAC=clang++

OUT_PATH=./bin

SRC_PATH_MAC= -I ./ *.cpp   -I./GL_Dependency/include/GL -I/usr/local/include -I./src/leveldb/include
SRC_PATH_LINUX=-I./src -I/usr/include/openssl -I/usr/local/include -I./src/leveldb/include

FILES_MAC= ./
FILES_LINUX= ./


CFLAGS_MAC=  -Bstatic -L/usr/local/lib   -lz   -std=c++11 -stdlib=libc++ -Wdeprecated -Wc++98-compat -w    `pkg-config --cflags --libs nice`

CFLAGS_LINUX= -L/usr/local/lib/ 

all:
	mkdir -p ${OUT_PATH}
	${CC_MAC} ${CFLAGS_MAC} ${SRC_PATH_MAC} ${FILES_MAC} -o ${OUT_PATH}/Dash

mac:
	mkdir -p ${OUT_PATH}
	${CC_MAC} ${CFLAGS_MAC} ${SRC_PATH_MAC} ${FILES_MAC} -o ${OUT_PATH}/Dash

linux:
	mkdir -p ${OUT_PATH}
	${CC_MAC} ${CFLAGS_LINUX} -std=c++11 ${SRC_PATH_LINUX} ${FILES_LINUX} -o ${OUT_PATH}/Dash



