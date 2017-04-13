/*-----------------------------------------------
  名称：系统初始化函数库
  编写：夏杰
  日期：2017.4.
  内容：按时序调用各个模块的初始化程序
------------------------------------------------*/

#include "sc.h"

/*-----------------------------------------------
  TODO：初始化sc系统
  参数：null
  编写：夏杰
  日期：2017.4.
------------------------------------------------*/
void sc_init()
{
	// 舵机初始化
	steer_init();
	// 
	motor_init();
	//
	blueteeth_init();
	//
	timer_init();

}