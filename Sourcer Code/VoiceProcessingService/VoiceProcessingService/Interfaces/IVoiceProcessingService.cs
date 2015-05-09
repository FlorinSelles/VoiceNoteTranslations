using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using System.IO;

namespace VoiceProcessingService.Interfaces
{
    [ServiceContract]
    public interface IVoiceProcessingService
    {
        [OperationContract]
        void UpLoadAudioNote(String audioNoteJson);

        [OperationContract]
        void UploadAudioNoteFile(Stream audioNoteFile);

        [OperationContract]
        void UpLoadAudioNoteTranslation(string audioNoteTranslationJson);

        [OperationContract]
        void UpLoadAudioNoteTranslationFile(Stream audioNoteTranslationFile);
        
        [OperationContract]
        string DownloadAllAudioNotes();

        [OperationContract]
        Stream DownloadAudioNoteFile(string audioNoteFileName);

        [OperationContract]
        string DownloadAudioNoteTranslations();

        [OperationContract]
        Stream DownloadAudioNoteTranslationFile(string audioNoteTranslationFileName);

        [OperationContract]
        string DownloadAllLanguages();

    }
}
