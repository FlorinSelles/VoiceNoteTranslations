using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using VoiceProcessingService.Interfaces;
using System.IO;
using Newtonsoft.Json;
using VoiceProcessingService.Models;
using System.Data.Entity;

namespace VoiceProcessingService.Service
{
    public class VoiceProcessingService: IVoiceProcessingService
    {
        private const String SERVER_DATA_DIRECTORY = @"E:\Server Data\";
        private static AudioNoteJson AudioNote;
        private static AudioTranslationJson AudioTranslation;

        public void UpLoadAudioNote(string audioNoteJson)
        {
            AudioNote = null;
            AudioNote = JsonConvert.DeserializeObject<AudioNoteJson>(audioNoteJson);
        }

        public void UploadAudioNoteFile(Stream audioNoteFile)
        {
            this.CopyStream(audioNoteFile, SERVER_DATA_DIRECTORY + AudioNote.audioFileName);

            voiceNoteTranslationEntities8 dataBase = new voiceNoteTranslationEntities8();

            audioNote voiceNote = new audioNote();
            voiceNote.audioFileName = AudioNote.audioFileName;
            voiceNote.description = AudioNote.description;

            language language = (from current in dataBase.languages where current.languageName == AudioNote.audioMainLanguage.languageName select current).First();
            voiceNote.idLanguage = language.id;

            dataBase.audioNotes.Add(voiceNote);
            dataBase.SaveChanges();
        }

        public void UpLoadAudioNoteTranslation(string audioNoteTranslationJson)
        {
            AudioTranslation = null;
            AudioTranslation = JsonConvert.DeserializeObject<AudioTranslationJson>(audioNoteTranslationJson);
        }

        public void UpLoadAudioNoteTranslationFile(Stream audioNoteTranslationFile)
        {
            this.CopyStream(audioNoteTranslationFile, SERVER_DATA_DIRECTORY + AudioTranslation.translationFileName);

            voiceNoteTranslationEntities8 dataBase = new voiceNoteTranslationEntities8();

            audioNote audio = (from current in dataBase.audioNotes where current.audioFileName == AudioTranslation.audioId.audioFileName select current).First();
            language language = (from current in dataBase.languages where current.languageName == AudioTranslation.languageId.languageName select current).First();

            audioTranslation tranlation = new audioTranslation();
            tranlation.idAudio = audio.id;
            tranlation.idLanguage = language.id;
            tranlation.translationFileName = AudioTranslation.translationFileName;

            int amount = (from current in dataBase.audioTranslations where current.translationFileName == tranlation.translationFileName select current).Count();
            if (amount > 0)
            {
                return;
            }

            dataBase.audioTranslations.Add(tranlation);
            dataBase.SaveChanges();
        }

        public string DownloadAllAudioNotes()
        {
            voiceNoteTranslationEntities8 dataBase = new voiceNoteTranslationEntities8();

            string audioNoteTable = JsonConvert.SerializeObject(dataBase.audioNotes);

            return audioNoteTable;
        }

        public Stream DownloadAudioNoteFile(string audioNoteFileName)
        {
            voiceNoteTranslationEntities8 database = new voiceNoteTranslationEntities8();

            audioNote voiceNote = (from current in database.audioNotes where current.audioFileName == audioNoteFileName select current).First();

            FileStream file = File.Open(SERVER_DATA_DIRECTORY + voiceNote.audioFileName, FileMode.Open);
            return file;
        }

        public string DownloadAudioNoteTranslations()
        {
            voiceNoteTranslationEntities8 dataBase = new voiceNoteTranslationEntities8();

            string audioNoteTranslationTable = JsonConvert.SerializeObject(dataBase.audioTranslations);

            return audioNoteTranslationTable;
        }

        public Stream DownloadAudioNoteTranslationFile(string audioNoteTranslationFileName)
        {
            voiceNoteTranslationEntities8 database = new voiceNoteTranslationEntities8();

            audioTranslation voiceNoteTranslation = (from current in database.audioTranslations where current.translationFileName == audioNoteTranslationFileName select current).First();

            FileStream file = File.Open(SERVER_DATA_DIRECTORY + voiceNoteTranslation.translationFileName, FileMode.Open);
            return file;
        }

        private void CopyStream(Stream stream, string filePath)
        {
            using (var fileStream = new FileStream(filePath, FileMode.Create, FileAccess.Write))
            {
                stream.CopyTo(fileStream);
            }
        }
        
        public string DownloadAllLanguages()
        {
            voiceNoteTranslationEntities8 dataBase = new voiceNoteTranslationEntities8();
            DbSet<language> languagesTable = dataBase.languages;

            languagesTable.Load();


            Object[] elementes = languagesTable.Select(language => new
            {
                language.id, language.languageName
            }).ToArray();
            
            string languageTablaJson = JsonConvert.SerializeObject(elementes);

            return languageTablaJson;
        }
    }
}