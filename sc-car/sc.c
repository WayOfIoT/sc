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

	DelayMs(500);

	// 
	led_wirte_str(20, 0, "xzit-Xia Jie");

	while(1)
	{
		// 电平驱动左电机正转
		motor_left_in = MOTOR_ON;
		motor_left_out = MOTOR_OFF;
		// 电平驱动右电机正转	
		motor_right_in = MOTOR_ON;
		motor_right_out = MOTOR_OFF;
		steer_duty = steer_left_duty;
		if(led_refresh_flag == 1)
		{
			led_refresh_flag = 0;
			led_write_num(10, 2, rec_data);
		}


	}


}














