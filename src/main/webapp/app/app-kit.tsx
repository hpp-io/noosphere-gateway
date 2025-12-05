'use client';

import { initializeWagmi } from 'app/config/wallet-config';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { createAppKit } from '@reown/appkit/react';
import { mainnet, sepolia } from '@reown/appkit/networks';
import React, { type ReactNode, useState, useEffect } from 'react';
import { cookieToInitialState, WagmiProvider, type Config } from 'wagmi';
import axios from 'axios';

const queryClient = new QueryClient();

const metadata = {
  name: 'Noosphere Gateway',
  description: 'Noosphere Gateway',
  url: 'https://nsapp.hpp.io',
  icons: ['https://avatars.githubusercontent.com/u/179229932'],
};

// prettier-ignore
function AppkitProvider({ children, cookies }: { children: ReactNode; cookies: string | null }) {
  const [config, setConfig] = useState<{ wagmiAdapter: any; config: any } | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchConfig = async () => {
      try {
        const response = await axios.get('/api/server/info');
        const { projectId, envValue } = response.data;
        const wagmiConfig = initializeWagmi(projectId, envValue);
        setConfig(wagmiConfig);

        if (envValue === 'test2' || envValue === 'test' ){
          metadata.url = "https://nstest.hpp.io"
        }

        createAppKit({
          adapters: [wagmiConfig.wagmiAdapter],
          projectId,
          networks: [mainnet, sepolia],
          defaultNetwork: mainnet,
          metadata,
          themeMode: 'light',
          allowUnsupportedChain: true,
          featuredWalletIds: [
              'c57ca95b47569778a828d19178114f4db188b89b763c899ba0be274e97267d96',
              'a797aa35c0fadbfc1a53e7f675162ed5226968b44a19ee3d24385c64d1d3c393'
          ],
          debug: envValue === 'production' ? false : true,
          enableWalletGuide: true,
          allWallets: 'HIDE',
          enableWalletConnect: true,
          termsConditionsUrl: 'https://paper.hpp.io/HPP_TermsConditions_v1.4.pdf',
          privacyPolicyUrl: 'https://paper.hpp.io/HPP_PrivacyPolicy_v1.6.pdf',
          features: {
            legalCheckbox: true,
            analytics: true,
            swaps: false,
            onramp: false,
            socials: false,
            email: false,
          },
        });
      } catch (error) {
        console.error('Failed to fetch app kit config', error);
      } finally {
        setLoading(false);
      }
    };

    fetchConfig();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!config) {
    return <div>Error: Failed to load configuration</div>;
  }

  const initialState = cookieToInitialState(config.config, cookies);
  return (
      <WagmiProvider config={config.config} initialState={initialState}>
        <QueryClientProvider client={ queryClient }>{ children }</QueryClientProvider>
      </WagmiProvider>
  );
}

export default AppkitProvider;
