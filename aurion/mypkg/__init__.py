#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: Mehdi-H
# @Date:   2015-05-28 18:05:45
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-05-28 18:09:36

from selenium import webdriver

driver = None

def getOrCreateWebdriver():
    global driver
    driver = driver or webdriver.Firefox()
    return driver