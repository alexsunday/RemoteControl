#!/usr/bin/env python
# encoding: utf-8
'''
Created on 2015年3月4日
len 1
src enum kbd, mouse ,sleep1
category, dbclick, keydown, keypress 1  mouse_click, mouse_dbclick, mouse_move, mouse_press mouse_down, key_down, key_press, key_click, sleep
value,  1

@author: Sunday
'''
import win32gui
import win32con
import win32api


from twisted.internet import protocol
from twisted.internet import reactor


class Event(object):
    class CATEGORY(object):
        KBD = 0
        MOUSE = 1
        SLEEP = 2

    def __init__(self, length, src, category, value):
        self.length = length
        self.src = src
        self.category = category
        self.value = value

    def __str__(self, *args, **kwargs):
        return '<Event src: [%d], category: [%d], value: [%d]' %\
            (self.src, self.category, self.value)


class Server(protocol.Protocol):
    def connectionMade(self):
        print self.transport.getPeer()
        self.buf = ''

    def connectionLost(self, reason):
        print reason.getErrorMessage()

    def get_package(self):
        if not self.buf:
            return None
        length = ord(self.buf[0])
        if len(self.buf) >= length:
            pack = self.buf[:length]
            self.buf = self.buf[length:]
            return pack
        return None

    def kbd_ev(self, ev):
        win32api.keybd_event(ev.value, 0, ev.category, 0)

    def mouse_ev(self, ev):
        pass

    def sleep_ev(self, ev):
        pass

    def event_dispatch(self, event):
        if event.category == Event.CATEGORY.KBD:
            self.kbd_ev(event)
        elif event.category == Event.CATEGORY.MOUSE:
            self.mouse_ev(event)
        elif event.category == Event.CATEGORY.SLEEP:
            self.sleep_ev(event)

    def dataReceived(self, data):
        print '%r' % data
        self.buf += data
        while True:
            pack = self.get_package()
            if not pack:
                break
            event = Event(ord(pack[0]), ord(pack[1]),\
                          ord(pack[2]), ord(pack[3]))
            print event
            self.event_dispatch(event)


def main():
    factory = protocol.Factory()
    factory.protocol = Server
    reactor.listenTCP(8888, factory)  # @UndefinedVariable
    reactor.run()  # @UndefinedVariable


if __name__ == '__main__':
    main()
