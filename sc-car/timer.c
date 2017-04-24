/*-----------------------------------------------
  ���ƣ���ʱ���⺯��
  ��д���Ľ�
  ���ڣ�2017.4.10
  ���ݣ���ʱ����ʼ�����жϺ���
------------------------------------------------*/
#include "sc.h"

// 100us, 1ms, 1s ��ʱ������
uchar timer_100us_counter = 0, timer_ms_counter = 0, timer_s_counter = 0;

/*-----------------------------------------------
  TODO����ʱ����ʼ��
  ������
  ��д���Ľ�
  ���ڣ�2017.4.10
------------------------------------------------*/
void timer_init()
{
	// ��ʱ��0,1��ʼ��
	TMOD = 0x11;

	// ��ʱ��0��ʱ��ֵ�趨
	TH0 = (TIMER_FULL_T - TIMER0_T) / 256;	//��ʱ100us,,��0.1ms
	TL0 = (TIMER_FULL_T - TIMER0_T) % 256;
	// ��ʱ��1��ʱ��ֵ�趨
	TH1 = (TIMER_FULL_T - TIMER1_T) / 256;	//��ʱ200us,,��0.2ms
	TL1 = (TIMER_FULL_T - TIMER1_T) % 256;

	// ʹ�����ж�
	EA = 1;

	// ʹ���ڲ��ж�0,1
	ET0 = 1;
	ET1 = 1;
	
	// ������ʱ��0,1(start timer0,timer1)
	TR0 = 1;
	TR1 = 0;	
}

/*-----------------------------------------------
  TODO����ʱ��0�жϺ���
  ������
  ��д���Ľ�
  ���ڣ�2017.4.10
------------------------------------------------*/
void timer0() interrupt 1 using 1
{	
	// pause timer0
	TR0 = 0;

	// ��ʱ������
	TH0 = (TIMER_FULL_T - TIMER0_T) / 256;	//��ʱ100us,,��0.1ms
	TL0 = (TIMER_FULL_T - TIMER0_T) % 256;

	// ��ʱ���������ۼ�, timer_100us_counter
	timer_100us_counter++;
	// 1ms��ʱ
	if(timer_100us_counter >= 10)
	{
		timer_100us_counter = 0;
		timer_ms_counter++;
		// 200ms ��ʱ
		if((timer_ms_counter+1) % 200 == 0)
		{
			timer_ms_counter = 0;
			// Һ��ˢ�±�ʶλ
			led_refresh_flag = 1;
			timer_s_counter++;
			if(timer_s_counter >= 250)
			{
				timer_s_counter = 0;
			}	
		}
		// ��ʱ 1s

	}

	/*---------------- ������� -----------------*/
	// ����������ۼ�
	steer_pwm_counter++;
	// ���PWM�����ڿ���
	if(steer_pwm_counter == steer_pwm_t)
	{
		steer_pwm_counter = 0;
	}
	// �ߵ�ƽռ��
	if(steer_pwm_counter <= steer_duty)	//PWM����
		steer_out = 1;
	else
		steer_out = 0;	
		

	// start timer0
	TR0 = 1;
}



void timer1() interrupt 3 using 2
{
	// pause timer1
//	TR1 = 0;

	// ��ʱ��1��ʱֵ����
	TH1 = (TIMER_FULL_T - TIMER1_T) / 256;	//��ʱ100us,,��0.1ms
	TL1 = (TIMER_FULL_T - TIMER1_T) % 256; 

	// ���PWM��������
	motor_pwm_counter++;
	// �������
	motor_control(motor_left_dir_back, 10, motor_right_dir_forward, 10);

	// start timer1
//	TR1 = 1;
}

























