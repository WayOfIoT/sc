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

	// 
	led_wirte_str(20, 0, "xzit-Xia Jie");

	while(1)
	{
		// ��ƽ����������ת
		motor_left_in = MOTOR_ON;
		motor_left_out = MOTOR_OFF;
		// ��ƽ�����ҵ����ת	
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














