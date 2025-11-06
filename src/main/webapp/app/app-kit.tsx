'use client';

import { projectId, wagmiAdapter } from 'app/config/wallet-config';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { createAppKit } from '@reown/appkit/react';
import { mainnet, sepolia } from '@reown/appkit/networks';
import React, { type ReactNode } from 'react';
import { cookieToInitialState, WagmiProvider, type Config } from 'wagmi';

const queryClient = new QueryClient();

if (!projectId) {
  throw new Error('Project ID is not defined');
}

const metadata = {
  name: 'Noosphere Gateway',
  description: 'Noosphere Gateway',
  url: 'https://www.hpp.io',
  icons: ['https://avatars.githubusercontent.com/u/179229932'],
};

// Create the AppKit
createAppKit({
  adapters: [wagmiAdapter],
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
  // includeWalletIds: ['c57ca95b47569778a828d19178114f4db188b89b763c899ba0be274e97267d96'],
  debug: process.env.HPP_ENV === 'production' ? false : true,
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

// prettier-ignore
function AppkitProvider({ children, cookies }: { children: ReactNode; cookies: string | null }) {
  const initialState = cookieToInitialState(wagmiAdapter.wagmiConfig, cookies);
  return (
      <WagmiProvider config={wagmiAdapter.wagmiConfig} initialState={initialState}>
        <QueryClientProvider client={ queryClient }>{ children }</QueryClientProvider>
      </WagmiProvider>
  );
}

export default AppkitProvider;
