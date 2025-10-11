package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.shell.interactive.enabled=false"})
class CommonHwTest {

    @Test
    void shouldNotContainConfigurationAnnotationAboveAppProperties() {
        assertThat(ru.otus.hw.config.AppProperties.class.isAnnotationPresent(Configuration.class))
                .withFailMessage("Класс свойств не является конфигурацией т.к. " +
                        "конфигурация для создания бинов, а тут просто компонент группирующий свойства приложения")
                .isFalse();
    }

    @Test
    void shouldNotContainPropertySourceAnnotationAboveAppProperties() {
        assertThat(ru.otus.hw.config.AppProperties.class.isAnnotationPresent(PropertySource.class))
                .withFailMessage("Аннотацию @PropertySource лучше вешать над конфигурацией, " +
                        "а класс свойств ей не является")
                .isFalse();
    }

    @Test
    void shouldNotContainFieldInjectedDependenciesOrProperties() {
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(org.springframework.stereotype.Component.class));
        provider.addIncludeFilter(new AnnotationTypeFilter(org.springframework.stereotype.Service.class));
        provider.addIncludeFilter(new AnnotationTypeFilter(org.springframework.stereotype.Repository.class));

        var beanDefinitions = provider.findCandidateComponents(Application.class.getPackageName());

        var classesWithFieldInjection = beanDefinitions.stream()
                .filter(bd -> {
                    try {
                        var clazz = Class.forName(bd.getBeanClassName());
                        return Arrays.stream(clazz.getDeclaredFields())
                                .anyMatch(f -> f.isAnnotationPresent(Autowired.class) || f.isAnnotationPresent(Value.class));
                    } catch (ClassNotFoundException e) {
                        return false;
                    }
                })
                .map(bd -> bd.getBeanClassName())
                .collect(Collectors.toList());

        assertThat(classesWithFieldInjection)
                .withFailMessage("На курсе все внедрение рекомендовано осуществлять через конструктор (" +
                        "в т.ч. @Value). Следующие классы нарушают это правило: %n%s"
                                .formatted(String.join("%n", classesWithFieldInjection)))
                .isEmpty();
    }
}