local socket = require 'socket'
local adress = '127.0.0.1'
local port = 8080


socket:connect(adress, port)