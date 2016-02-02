#!/usr/bin/env python
# -*- coding: utf-8 -*-
#导入
import xlrd
import os
import stat

def getNum(s):
	tem = ''
	for ch in s:
		tem = tem+str2str(ch)
	return tem
	
def str2str(s):
	#	return {',':'','`':'',';':'',';':'',' ':'','_':'','1':'27','2':'28','3':'29','/':'30','4':'31','5':'32','6':'33','7':'34','8':'35','9':'36','A':'1','B':'2','C':'3','D':'4','E':'5','F':'6','G':'7','H':'8','I':'9','J':'10','K':'11','L':'12','M':'13','N':'14','O':'15','P':'16','Q':'17','R':'18','S':'19','T':'20','U':'21','V':'22','w':'23','W':'23','X':'24','Y':'25','Z':'26'}[s]
	return {',':'','`':'',';':'',';':'',' ':'','_':'','1':'27','2':'28','3':'29','/':'30','4':'31','5':'32','6':'33','7':'34','8':'35','9':'36','A':'1','B':'2','C':'3','D':'4','E':'5','F':'6','G':'7','H':'8','I':'9','J':'10','K':'11','L':'12','M':'13','N':'14','O':'15','P':'16','Q':'17','R':'18','S':'19','T':'20','U':'21','V':'22','w':'23','W':'23','X':'24','Y':'25','Z':'26'}[s]
	
class Address(object):
	id = '';
	parent_id= '';
	name= '';
	code= '';
	priority= '';
	
	def __init__(self,parent_id, id, name,code ,priority ):
		
		if(parent_id==''):
			self.parent_id =''
		else:
			self.parent_id = getNum(parent_id)
		self.id =  getNum(id)
		self.name =  name
		self.code =  code
		self.priority =  priority

	def printAll(self):
		print self.id ,self.parent_id,self.name,self.code,self.priority
	
	#insert into b2c_address_jc(id, parent_id, name, priority, code, disabled,  type) values(1,1,'22',2,'cc',1,3);
	#生成sql语句
	def genSql(self): 
		return 'insert into b2c_address_jc(id, parent_id, name, priority, code, disabled,  type) values(' + self.id + ','+self.parent_id+',\''+self.name+'\','+self.priority+',\''+self.code+'\',1,3);';

	
#打开excel
data = xlrd.open_workbook('D:/tem/address.xls') #注意这里的workbook首字母是小写
#查看文件中包含sheet的名称
data.sheet_names()
#得到第一个工作表，或者通过索引顺序 或 工作表名称
table = data.sheets()[0]
#循环行,得到索引的列表
adds = {};
for rownum in range(table.nrows):
	#获取整行数据
	rowData =  table.row_values(rownum)
	#省
	pro_name = rowData[0]
	#省代码
	pro_code = rowData[1]
	#市
	city_name = rowData[2]
	#市代码
	city_code = rowData[3]
	#区
	sub_city_name = rowData[4]
	#区代码
	sub_city_code = rowData[5]
	#镇
	area_name = rowData[6]
	#镇代码
	area_code = rowData[7]
	
	# __init__(self,parent_id, id, name,code ,priority )
	pro = Address('',pro_code,pro_name,pro_code,'1')
	city =Address(pro_code,pro_code+city_code,city_name,city_code,'2')
	sub_city =Address(pro_code+city_code,pro_code+city_code+sub_city_code,sub_city_name,sub_city_code,'3')
 	area =Address(pro_code+city_code+sub_city_code,pro_code+city_code+sub_city_code+area_code,area_name,area_code,'4')
	
 	adds[pro.id] = pro
	adds[city.id] = city
	adds[sub_city.id] = sub_city
	adds[area.id] = area

f = file('D:/tem/genAddress.sql','w')
for v in adds.itervalues():
	f.write(v.genSql().encode('utf-8'))
	f.write('\n')

f.close()

print file('D:/tem/genAddress.sql','r').read()
