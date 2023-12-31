package dev.emreuygun;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DependsOnPostProcessor implements BeanFactoryPostProcessor {

    final Class<?> beansOfType;
    final String[] dependsOn;

    protected DependsOnPostProcessor(Class<?> beansOfType, String ... dependsOn) {
        this.beansOfType = beansOfType;
        this.dependsOn = dependsOn;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        List<String> beanNamesForType = asList(getBeanNamesForType(beanFactory));
        beanNamesForType.forEach(datastoreClientBeanName -> setupDependsOn(beanFactory, datastoreClientBeanName)
        );
    }

    private void setupDependsOn(ConfigurableListableBeanFactory beanFactory, String dataSourceBeanName) {
        BeanDefinition dataStoreClientBeanDefinition = beanFactory.getBeanDefinition(dataSourceBeanName);
        List<String> dependsOnExisting = asList(dataStoreClientBeanDefinition.getDependsOn());
        dependsOnExisting.addAll(asList(dependsOn));
        dataStoreClientBeanDefinition.setDependsOn(dependsOnExisting.toArray(new String[]{}));
    }

    private String[] getBeanNamesForType(ConfigurableListableBeanFactory beanFactory) {
        boolean includeNonSingletons = true;
        boolean allowEagerInit = true;
        return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                beanFactory,
                beansOfType,
                includeNonSingletons,
                allowEagerInit
        );
    }

    private static List<String> asList(String[] array) {
        return (array == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(array)));
    }
}