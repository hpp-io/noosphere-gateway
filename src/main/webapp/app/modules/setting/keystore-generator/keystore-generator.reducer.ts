import { createAsyncThunk, createSlice, isPending, isRejected } from '@reduxjs/toolkit';
import axios from 'axios';
import { serializeAxiosError } from 'app/shared/reducers/reducer.utils';

const initialState = {
  loading: false,
  errorMessage: null,
  keystoreFile: null as Blob | null,
  fileName: '',
  validatedValue: null as string | null,
};

export type KeystoreGeneratorState = Readonly<typeof initialState>;

const fileToBase64 = (file: File): Promise<string> =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve((reader.result as string).split(',')[1]);
    reader.onerror = error => reject(new Error('Failed to read file'));
  });

export const createKeystore = createAsyncThunk(
  'keystore/create',
  async (data: { keyAlias: string; password: string; privateKey: string }) => {
    const response = await axios.post('/api/keystore/create', data, {
      responseType: 'blob',
    });
    return {
      file: response.data,
      fileName: `${data.keyAlias}.p12`,
    };
  },
  {
    serializeError: serializeAxiosError,
  }
);

export const validateKeystore = createAsyncThunk(
  'keystore/validate',
  async (data: { file: File; keyAlias: string; password: string }) => {
    const fileContent = await fileToBase64(data.file);
    const response = await axios.post('/api/keystore/validate', {
      fileContent,
      keyAlias: data.keyAlias,
      password: data.password,
    });
    return response.data;
  },
  {
    serializeError: serializeAxiosError,
  }
);

export const KeystoreGeneratorSlice = createSlice({
  name: 'keystoreGenerator',
  initialState: initialState as KeystoreGeneratorState,
  reducers: {
    reset(state) {
      state.loading = false;
      state.errorMessage = null;
      state.keystoreFile = null;
      state.fileName = '';
      state.validatedValue = null;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(createKeystore.fulfilled, (state, action) => {
        state.loading = false;
        state.keystoreFile = action.payload.file;
        state.fileName = action.payload.fileName;
      })
      .addCase(validateKeystore.fulfilled, (state, action) => {
        state.loading = false;
        state.validatedValue = action.payload;
      })
      .addMatcher(isPending(createKeystore, validateKeystore), state => {
        state.errorMessage = null;
        state.loading = true;
        state.keystoreFile = null;
        state.fileName = '';
        state.validatedValue = null;
      })
      .addMatcher(isRejected(createKeystore, validateKeystore), (state, action) => {
        state.loading = false;
        state.errorMessage = action.error.message;
      });
  },
});

export const { reset } = KeystoreGeneratorSlice.actions;
export default KeystoreGeneratorSlice.reducer;
