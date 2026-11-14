import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { DEFAULT_LANGUAGE, SUPPORTED_LANGUAGES, translations } from './i18n';

const I18nContext = createContext(null);

/**
 * I18n Provider Component
 * Manages language state and provides translation function
 */
export function I18nProvider({ children }) {
  const [language, setLanguage] = useState(() => {
    // Load from localStorage or default to English
    if (typeof window !== 'undefined') {
      const saved = localStorage.getItem('hiking-route-language');
      if (saved && SUPPORTED_LANGUAGES.some(l => l.code === saved)) {
        return saved;
      }
    }
    return DEFAULT_LANGUAGE;
  });

  const [isReady, setIsReady] = useState(false);

  // Persist language to localStorage
  useEffect(() => {
    localStorage.setItem('hiking-route-language', language);
    // Update document lang attribute
    document.documentElement.lang = language === 'pt' ? 'pt-BR' : 'en';
  }, [language]);

  // Mark as ready after first render (for SSR hydration)
  useEffect(() => {
    setIsReady(true);
  }, []);

  const t = useCallback((key, params = {}) => {
    const langDict = translations[language] || translations[DEFAULT_LANGUAGE];
    const keys = key.split('.');
    let value = langDict;

    for (const k of keys) {
      if (value && typeof value === 'object' && k in value) {
        value = value[k];
      } else {
        // Fallback to English
        value = translations[DEFAULT_LANGUAGE];
        for (const fk of keys) {
          if (value && typeof value === 'object' && fk in value) {
            value = value[fk];
          } else {
            return key;
          }
        }
        break;
      }
    }

    if (typeof value === 'string') {
      return value.replace(/\{(\w+)\}/g, (match, paramKey) => params[paramKey] ?? match);
    }

    return value;
  }, [language]);

  const getAlgorithmComplexity = useCallback((algorithm) => {
    const complexities = translations[language]?.algorithmComplexity || translations[DEFAULT_LANGUAGE].algorithmComplexity;
    return complexities[algorithm] || complexities.dijkstra;
  }, [language]);

  const value = {
    language,
    setLanguage,
    t,
    getAlgorithmComplexity,
    supportedLanguages: SUPPORTED_LANGUAGES,
    isReady,
  };

  return (
    <I18nContext.Provider value={value}>
      {children}
    </I18nContext.Provider>
  );
}

/**
 * Hook to use i18n in components
 */
export function useI18n() {
  const context = useContext(I18nContext);
  if (!context) {
    throw new Error('useI18n must be used within an I18nProvider');
  }
  return context;
}