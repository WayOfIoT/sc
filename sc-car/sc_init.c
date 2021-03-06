/*-----------------------------------------------
  名称：系统初始化函数库
  编写：夏杰
  日期：2017.4.11
  内容：按时序调用各个模块的初始化程序
------------------------------------------------*/

#include "sc.h"

/*-----------------------------------------------
  TODO：初始化sc系统
  参数：null
  编写：夏杰
  日期：2017.4.11
------------------------------------------------*/
void sc_init()
{
	// 舵机初始化
	steer_init();
	// 电机驱动初始化
	motor_init();
	// OLED初始化
	LED_init();
	// 蓝牙通信初始化
	blueteeth_init();

	// 定时器初始化
	timer_init();

}