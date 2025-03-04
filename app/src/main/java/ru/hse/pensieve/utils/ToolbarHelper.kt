package ru.hse.pensieve.utils

import android.widget.ImageButton

object ToolbarHelper {
    fun setupToolbarButtons(
        buttons: List<ImageButton>, // Список кнопок
        defaultIcons: List<Int>, // Список иконок по умолчанию
        selectedIcons: List<Int>, // Список иконок при нажатии
        onButtonClick: (Int) -> Unit // Лямбда-функция для обработки нажатий
    ) {
        buttons.forEachIndexed { index, button ->
            // Устанавливаем иконку по умолчанию
            button.setImageResource(defaultIcons[index])

            // Обработка нажатия на кнопку
            button.setOnClickListener {
                // Меняем иконку на выбранную
                button.setImageResource(selectedIcons[index])
                // Вызываем колбэк с индексом кнопки
                onButtonClick(index)
            }
        }
    }


}