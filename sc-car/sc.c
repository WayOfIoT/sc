/*-----------------------------------------------
  ���ƣ����������ļ�
  ��д���Ľ�
  ���ڣ�2017.4.1
  ���ݣ�������ڣ�����Ҫ���Ʋ���
------------------------------------------------*/
#include "sc.h"




/*-----------------------------------------------
  TODO��������
  ������
  ��д���Ľ�
  ���ڣ�2017.4.1
------------------------------------------------*/
void main()
{

	// ϵͳ��ʼ��
	sc_init();

	DelayMs(500);

	// Һ������
	led_wirte_str(20, 0, "xzit-Xia Jie");

	while(1)
	{
		// ͣ��
		if(rec_data == 0)
		{
			motor_left_stop();
			motor_right_stop();
			steer_middle();			
		} 
		// ǰ��
		else if(rec_data == 1) 
		{
			motor_left_forward(100);
			motor_right_forward(100);
			steer_middle();	
		}
		// ����
		else if(rec_data == 2) 
		{
			motor_left_back(100);
			motor_right_back(100);
			steer_middle();	
		}
		// ��ת
		else if(rec_data == 3) 
		{
			motor_left_forward(100);
			motor_right_forward(100);
			steer_left();	
		}
		// ��ת
		else if(rec_data == 4)
		{
			motor_left_forward(100);
			motor_right_forward(100);
			steer_right();
		}

		// Һ��ˢ��
		if(led_refresh_flag == 1)
		{
			led_refresh_flag = 0;
			led_write_num(10, 2, rec_data);
		}


	}


}














