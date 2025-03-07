package com.isge.demo.service;

import com.isge.demo.entity.Menu;

import java.util.List;

public interface MenuService {
    Menu createMenu(Menu menu);
    List<Menu> allMenus();
    Menu readMenu(String id);
    Menu updateMenu(Menu menu);
    void deleteMenu(String id);
}