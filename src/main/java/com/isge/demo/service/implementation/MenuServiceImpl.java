package com.isge.demo.service.implementation;

import com.isge.demo.entity.Menu;
import com.isge.demo.repository.MenuRepository;
import com.isge.demo.service.MenuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public Menu createMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    @Override
    public List<Menu> allMenus() {
        return menuRepository.findAll();
    }

    @Override
    public Menu readMenu(String id) {
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        return optionalMenu.orElse(null);
    }

    @Override
    public Menu updateMenu(Menu menu) {
        if (menuRepository.existsById(menu.getId())) {
            return menuRepository.save(menu);
        }
        return null; // Ou lever une exception si le menu n'existe pas
    }

    @Override
    public void deleteMenu(String id) {
        menuRepository.deleteById(id);
    }
}