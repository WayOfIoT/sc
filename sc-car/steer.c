/*-----------------------------------------------
  ���ƣ����ת������������
  ��д���Ľ�
  ���ڣ�2017.4.12
  ���ݣ����������غ���
------------------------------------------------*/

#include "sc.h"

// ���PWM�����ڡ�������
uint steer_pwm_t = 200, steer_pwm_counter = 0;

// ���PWMռ�ձ�,��Χ:5/200~25/200 
uchar steer_left_duty = 18, steer_right_duty = 12, steer_middle_duty = 15, steer_duty;

/*-----------------------------------------------
  TODO�������ʼ��
  ������null
  ��д���Ľ�
  ���ڣ�2017.4.12
------------------------------------------------*/
void steer_init()
{
	// �����������λ
	steer_pwm_counter = 0;	
	// �����ʼλ������
	steer_duty = steer_middle;
}

/*-----------------------------------------------
  TODO�����ת�����
  ������
  ��д���Ľ�
  ���ڣ�2017.4.12
------------------------------------------------*/
void steer_control(STEER_DIR steer_dir)
{
	// ���PWM�����ڿ���
	if(steer_pwm_counter >= steer_pwm_t)
	{
		steer_pwm_counter = 0;
	}
	// ���ת���ж�
	switch(steer_dir)
	{
		case steer_dir_left:
			steer_left();
			break;
		case steer_dir_middle:
			steer_middle();
			break;
		case steer_dir_right:
			steer_right();
			break;
	}
	// �ߵ�ƽռ��
	if(steer_pwm_counter <= steer_duty)	//PWM����
		steer_out = 1;
	else
		steer_out = 0;
}

/*-----------------------------------------------
  TODO�������ת��PWM��ռ�ձ�
  ������null
  ��д���Ľ�
  ���ڣ�2017.4.12
------------------------------------------------*/
void steer_left()
{
	steer_duty = steer_left_duty;
}

/*-----------------------------------------------
  TODO�������ת��PWM��ռ�ձ�
  ������null
  ��д���Ľ�
  ���ڣ�2017.4.12
------------------------------------------------*/
void steer_right()
{
	steer_duty = steer_right_duty;
}

/*-----------------------------------------------
  TODO���������PWM��ռ�ձ�
  ������null
  ��д���Ľ�
  ���ڣ�2017.4.12
------------------------------------------------*/
void steer_middle()
{
	steer_duty = steer_middle_duty;
}



