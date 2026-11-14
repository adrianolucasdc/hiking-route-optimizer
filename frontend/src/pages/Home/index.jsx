import { useState, useEffect } from 'react';
import { I18nProvider } from '../../lib/I18nContext';
import Header from '../../components/Header';
import Footer from '../../components/Footer';
import HikingRouteForm from '../../components/Forms/HikingRouteForm';

function HomeContent() {
  const [theme, setTheme] = useState('dark'); // dark is default

  useEffect(() => {
    const saved = localStorage.getItem('theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const initial = saved || (prefersDark ? 'dark' : 'light');
    setTheme(initial);
    document.documentElement.classList.toggle('light', initial === 'light');
  }, []);

  const toggleTheme = () => {
    const newTheme = theme === 'dark' ? 'light' : 'dark';
    setTheme(newTheme);
    localStorage.setItem('theme', newTheme);
    document.documentElement.classList.toggle('light', newTheme === 'light');
  };

  return (
    <div className="min-h-screen bg-bg flex flex-col">
      <Header currentTheme={theme} onThemeChange={toggleTheme} />

      <main className="flex-1 pt-20 pb-16 px-4">
        <HikingRouteForm />
      </main>

      <Footer />
    </div>
  );
}

export default function Home() {
  return (
    <I18nProvider>
      <HomeContent />
    </I18nProvider>
  );
}