local socket = require 'socket'
local adress = '0.0.0.0'
local port = 8080


socket:connect(adress, port)