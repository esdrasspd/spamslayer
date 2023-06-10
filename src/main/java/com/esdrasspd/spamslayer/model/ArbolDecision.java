package com.esdrasspd.spamslayer.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esdrasspd.spamslayer.repository.EmailRepository;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.*;

import java.util.Arrays;
import java.util.List;

@Component
public class ArbolDecision {

    private final EmailRepository emailRepository;
    private Instances data;
    private Classifier classifier;

    @Autowired
    public ArbolDecision(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public void construirArbolDecision() throws Exception {
        // Obtener los datos de entrenamiento desde la base de datos
        List<Email> datosEntrenamiento = emailRepository.findAll();
        System.out.println(datosEntrenamiento.size());
    
        // Crear un diccionario de palabras (vocabulario) a partir de los textos
        FastVector dictionary = new FastVector();
        for (Email email : datosEntrenamiento) {
            String remitente = email.getRemitente();
            String mensaje = email.getMensaje();
    
            // Preprocesamiento del remitente
            remitente = preprocesarTexto(remitente);
    
            // Preprocesamiento del mensaje
            mensaje = preprocesarTexto(mensaje);
    
            String[] palabras = remitente.split(" "); // Separa el remitente por espacios
            for (String palabra : palabras) {
                if (!dictionary.contains(palabra)) {
                    dictionary.addElement(palabra);
                }
            }
            palabras = mensaje.split(" "); // Separa el mensaje por espacios
            for (String palabra : palabras) {
                if (!dictionary.contains(palabra)) {
                    dictionary.addElement(palabra);
                }
            }
        }
        System.out.println(dictionary.toString());
    
        // Definir los atributos
        FastVector attributes = new FastVector();
        for (int i = 0; i < dictionary.size(); i++) {
            Attribute wordAttribute = new Attribute("word_" + i);
            attributes.addElement(wordAttribute);
        }
        FastVector classValues = new FastVector();
        classValues.addElement("No Spam");
        classValues.addElement("Spam");
        Attribute classAttribute = new Attribute("esSpam", classValues);
        attributes.addElement(classAttribute);
    
        // Crear el conjunto de datos
        this.data = new Instances("DatosEntrenamiento", attributes, datosEntrenamiento.size());
        data.setClass(classAttribute);
    
        // Agregar instancias al conjunto de datos
        for (Email email : datosEntrenamiento) {
            double[] values = new double[data.numAttributes()];
            String remitente = preprocesarTexto(email.getRemitente());
            String mensaje = preprocesarTexto(email.getMensaje());
    
            String[] palabras = remitente.split(" ");
            for (String palabra : palabras) {
                int index = dictionary.indexOf(palabra);
                if (index >= 0) {
                    values[index] = 1; // La palabra está presente en el remitente
                }
            }
            palabras = mensaje.split(" ");
            for (String palabra : palabras) {
                int index = dictionary.indexOf(palabra);
                if (index >= 0) {
                    values[index] = 1; // La palabra está presente en el mensaje
                }
            }
            values[data.numAttributes() - 1] = email.getEs_spam() ? 1 : 0; // Valor de la clase
            Instance instance = new SparseInstance(1.0, values);
            data.add(instance);
        }
    
        // Construir el árbol de decisión
        classifier = new J48();
        classifier.buildClassifier(data);
    }
    
    private String preprocesarTexto(String texto) {
        // Eliminar palabras vacías o stop words
        texto = eliminarStopWords(texto);
    
        // Eliminar caracteres especiales o puntuaciones
        texto = eliminarCaracteresEspeciales(texto);
    
        // Normalizar el texto (convertir a minúsculas)
        texto = texto.toLowerCase();
    
        return texto;
    }
    
    private String eliminarStopWords(String texto) {
        List<String> stopWords = Arrays.asList("el", "la", "los", "las", "de", "a", "y", "o");
        String[] palabras = texto.split(" ");
        StringBuilder resultado = new StringBuilder();
        for (String palabra : palabras) {
            if (!stopWords.contains(palabra)) {
                resultado.append(palabra).append(" ");
            }
        }
        return resultado.toString().trim();
    }
    
    private String eliminarCaracteresEspeciales(String texto) {
        texto = texto.replaceAll("[^a-zA-Z\\s]", "");
        return texto;
    }
    
    public boolean predecir(Email email) throws Exception {

    Instances testInstances = new Instances(data, 1);
    testInstances.setClassIndex(testInstances.numAttributes() - 1);

    Instance instance = new DenseInstance(testInstances.numAttributes());
    for (int i = 0; i < testInstances.numAttributes() - 1; i++) {
        String attributeName = testInstances.attribute(i).name();
        if (attributeName.startsWith("word_")) {
            String word = attributeName.substring(5);
            if (email.getRemitente().contains(word) || email.getMensaje().contains(word)) {
                instance.setValue(testInstances.attribute(i), 1);
            } else {
                instance.setValue(testInstances.attribute(i), 0);
            }
        }
    }
    testInstances.add(instance);

    double predictedClass = classifier.classifyInstance(testInstances.firstInstance());

    String predictedLabel = testInstances.attribute("esSpam").value((int) predictedClass);

    return predictedLabel.equals("Spam");
    
    }
}
