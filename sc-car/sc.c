/*-----------------------------------------------
  名称：程序主控文件
  编写：夏杰
  日期：2017.4.1
  内容：程序入口，及主要控制部分
------------------------------------------------*/
#include "sc.h"




/*-----------------------------------------------
  TODO：主函数
  参数：
  编写：夏杰
  日期：2017.4.1
------------------------------------------------*/
void main()
{

	// 系统初始化
	sc_init();

	while(1)
	{
		// 舵机控制
		steer_duty = steer_left_duty;
		DelayMs(200);
		steer_duty = steer_right_duty;
		DelayMs(200);

		// 电平驱动左电机正转
		motor_left_in = MOTOR_ON;
		motor_left_out = MOTOR_OFF;
		// 电平驱动	
		motor_right_in = MOTOR_ON;
		motor_right_out = MOTOR_OFF;
	}


}














