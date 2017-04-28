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

	// 液晶布局
	led_wirte_str(20, 0, "xzit-Xia Jie");

	while(1)
	{
		// 停车
		if(rec_data == 0)
		{
			motor_left_stop();
			motor_right_stop();
			steer_middle();			
		} 
		// 前进
		else if(rec_data == 1) 
		{
			motor_left_forward(100);
			motor_right_forward(100);
			steer_middle();	
		}
		// 倒车
		else if(rec_data == 2) 
		{
			motor_left_back(100);
			motor_right_back(100);
			steer_middle();	
		}
		// 左转
		else if(rec_data == 3) 
		{
			motor_left_forward(100);
			motor_right_forward(100);
			steer_left();	
		}
		// 右转
		else if(rec_data == 4)
		{
			motor_left_forward(100);
			motor_right_forward(100);
			steer_right();
		}

		// 液晶刷新
		if(led_refresh_flag == 1)
		{
			led_refresh_flag = 0;
			led_write_num(10, 2, rec_data);
		}


	}


}














