#!/usr/bin/env python

from selenium import webdriver
import time 
from lxml import etree 
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import json, os
import datetime
__author__ = 'JingAi Zhang'

####################################################################################
username = "jazzhang"
password = "19810318abc"
Fixtime = "10:01:00"
####################################################################################
DEBUG = False
if DEBUG:
	print("In debug mode")


class Login_TB(object):
	def __init__(self,username,password):
		self.username = username
		self.password = password
		self.URL = 		"https://pub.alimama.com/"
		self.login_web = 'https://pub.alimama.com/'
		self.login()

	def longin_and_save_cookies(self):
		print("===>1")
		browser.get(self.login_web)
		time.sleep(3)

		print ('login with account')
		login_frame_e = browser.find_element_by_xpath('//*[@id="mx_n_17"]/div/iframe')
		login_frame = browser.switch_to.frame(login_frame_e)


		browser.find_element_by_id('J_Quick2Static').click() 
		print("===> scan the image with your taobao app")
		###due to limited slip bar, currently, we cannot login with username 
		'''
		time.sleep(1)
		user = browser.find_element_by_id('TPL_username_1') 
		user.clear() 
		pwd = browser.find_element_by_id('TPL_password_1') 
		pwd.clear() 
		submit = browser.find_element_by_id('J_SubmitStatic') 
		time.sleep(1) 
		user.send_keys(self.username) 
		pwd.send_keys(self.password) 
		submit.click() 
		##button or link is choosed
		#need to drag the pic in the web 6 send is necessary
		'''
		time.sleep(30)
		
		JD_cookies = browser.get_cookies()
		#for cookie in JD_cookies:
		#	print("%s--->%s\n" % (cookie['name'], cookie['value']))

		jsonCookies = json.dumps(JD_cookies)
		# 登录完成后，将cookie保存到本地文件
		with open('TB_cookies.json', 'w') as f:
			f.write(jsonCookies)


	def login_with_cookie(self):
		browser.get(self.URL)
		# 读取登录时存储到本地的cookie
		# 删除第一次建立连接时的cookie
		#browser.delete_all_cookies()
		print("===>3")
		with open('TB_cookies.json', 'r', encoding='utf-8') as f:
			listCookies = json.loads(f.read())
		print("===>3.5")
		for cookie in listCookies:
			browser.add_cookie({
				'domain': '.alimama.com',  # chrome一定要有这个
				'name': cookie['name'],
				'value': cookie['value'],
				'path': '/',
				'expires': None
			})
		# 再次访问页面，便可实现免登陆访问
		browser.get(self.URL)
		browser.execute_script("window.scrollTo(0,document.body.scrollHeight)")
		time.sleep(5)

	def save_cookies(self):
		JD_cookies = browser.get_cookies()
		#for cookie in JD_cookies:
		#	print("%s--->%s\n" % (cookie['name'], cookie['value']))

		jsonCookies = json.dumps(JD_cookies)
		# 登录完成后，将cookie保存到本地文件
		with open('TB_cookies.json', 'w') as f:
			f.write(jsonCookies)

	def login(self):
		if (os.path.exists("TB_cookies.json")):
			print("have cookies file")
			self.login_with_cookie()
		else:
			print("Firest time : Need to login and save the cookie:")
			self.longin_and_save_cookies()
	
	def get_alimama_daily_coupon(self):
		browser.get("https://pub.alimama.com/myunion.htm#!/promo/self/items")
		browser.maximize_window()
		time.sleep(4)
		try:

			#click "知道了"
			browser.find_element_by_xpath('//*[@id="brix_89"]/div[3]/div/span[2]').click()
			print("delete I Know ")
		except Exception as e:
			print("==>Exception found", format(e))
		browser.execute_script("window.scrollTo(0,(document.body.scrollHeight)/3)")
		time.sleep(5)	
		#点击需要的excel button
		browser.find_element_by_xpath('/html/body/vframe/div/div/div[2]/div/div/vframe/div/div/vframe/div/vframe/div/div/div[3]/div[3]/button').click()
		print("==>6")
		time.sleep(4)
		browser.find_element_by_xpath('/html/body/div[5]/div/vframe/div/div/button').click()
		print("=====>start download the excel file 30MBytes,need to sleep 3mins")
		time.sleep(120)
		print("=====>sleep done, the excel is donwled in current folder")


if __name__ == "__main__":

	browser = webdriver.Chrome()
	#browser = webdriver.Firefox()

	TB = Login_TB(username,password)
	TB.get_alimama_daily_coupon()
	browser.quit()
	'''
	while True:
		now = datetime.datetime.now()
		if now.strftime('%H:%M:%S') == Fixtime:
			browser = webdriver.Chrome()
			TB = Login_TB(username,password)
			TB.get_alimama_daily_coupon()
			browser.quit()
			time.sleep(60)			
		else:
			print("==>time is %s" % now.strftime('%H:%M:%S')  )
			time.sleep(10)
			#update the cookies.here
	'''

